package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.VideoStatusResponse;
import com.devacademy.DevAcademy_BE.dto.VideoUploadTask;
import com.devacademy.DevAcademy_BE.enums.VideoUploadStatus;
import com.devacademy.DevAcademy_BE.repository.LessonRepository;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VideoUploadQueueService {
    static int MAX_QUEUE_SIZE = 1000;
    static int THREAD_POOL_SIZE = 5;
    static String REDIS_VIDEO_STATUS_KEY = "video:status:";

    BlockingQueue<VideoUploadTask> queue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    LessonRepository lessonRepository;
    CloudinaryService cloudinaryService;
    RedisTemplate<String, VideoStatusResponse> redisTemplate;
    ExecutorService executorService;
    WebSocketService webSocketService;

    @Autowired
    public VideoUploadQueueService(
            LessonRepository lessonRepository,
            CloudinaryService cloudinaryService,
            RedisTemplate<String, VideoStatusResponse> redisTemplate, WebSocketService webSocketService
    ) {
        this.lessonRepository = lessonRepository;
        this.cloudinaryService = cloudinaryService;
        this.redisTemplate = redisTemplate;
        this.webSocketService = webSocketService;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            executorService.submit(this::processQueue);
        }
    }

    public boolean addToQueue(VideoUploadTask task) {
        boolean added = queue.offer(task);
        if (added) {
            updateVideoStatusInRedis(task.getLessonId(), VideoUploadStatus.PENDING, null, null);
        } else {
            log.warn("Queue đầy, không thể thêm task cho lessonId: {}", task.getLessonId());
        }
        return added;
    }

    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                VideoUploadTask task = queue.take();
                handleTask(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Worker thread bị ngắt");
            } catch (Exception e) {
                log.error("Lỗi xử lý task: {}", e.getMessage());
            }
        }
    }

    private void handleTask(VideoUploadTask task) {
        Long lessonId = task.getLessonId();
        try {
            updateVideoStatusInRedis(lessonId, VideoUploadStatus.PROCESSING, null, null);

            String videoUrl = cloudinaryService.uploadLargeVideo(task.getVideo());

            lessonRepository.findById(lessonId).ifPresent(lesson -> {
                lesson.setVideoUrl(videoUrl);
                lessonRepository.save(lesson);
            });

            updateVideoStatusInRedis(lessonId, VideoUploadStatus.DONE, videoUrl, null);

            log.info("Upload thành công cho lessonId: {}", lessonId);

        } catch (Exception e) {
            log.error("Error uploading video for lessonId {}: {}, task: {}",
                    lessonId, e.getMessage(), task.getVideo().getOriginalFilename());
        }
    }

    private void updateVideoStatusInRedis(Long lessonId, VideoUploadStatus status, String url, String error) {
        VideoStatusResponse response = VideoStatusResponse.builder()
                .lessonId(lessonId)
                .status(status.name())
                .videoUrl(url)
                .errorMessage(error)
                .build();

        redisTemplate.opsForValue().set(REDIS_VIDEO_STATUS_KEY + lessonId, response, Duration.ofMinutes(30));

        webSocketService.broadcastProgress(response);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
        log.info("VideoUploadQueueService đã tắt thread pool.");
    }
}
