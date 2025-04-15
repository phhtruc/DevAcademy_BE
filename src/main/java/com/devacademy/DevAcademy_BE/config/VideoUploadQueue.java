package com.devacademy.DevAcademy_BE.config;

import com.devacademy.DevAcademy_BE.dto.VideoUploadTask;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.repository.LessonRepository;
import com.devacademy.DevAcademy_BE.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class VideoUploadQueue {

    private final BlockingQueue<VideoUploadTask> queue = new LinkedBlockingQueue<>();
    private final LessonRepository lessonRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public VideoUploadQueue(LessonRepository lessonRepository, CloudinaryService cloudinaryService) {
        this.lessonRepository = lessonRepository;
        this.cloudinaryService = cloudinaryService;

        Thread worker = new Thread(this::processQueue);
        worker.setDaemon(true); // chạy ngầm
        worker.start();
    }

    public void enqueue(VideoUploadTask task) {
        queue.add(task);
    }

    private void processQueue() {
        while (true) {
            try {
                VideoUploadTask task = queue.take();
                String videoUrl = cloudinaryService.uploadLargeVideo(task.getVideo());
                LessonEntity lesson = lessonRepository.findById(task.getLessonId())
                        .orElseThrow(() -> new RuntimeException("Lesson not found during video upload"));
                lesson.setVideoUrl(videoUrl);
                lessonRepository.save(lesson);
                System.out.println("Upload complete for lesson: " + task.getLessonId());
            } catch (Exception e) {
                e.printStackTrace(); // hoặc log lỗi
            }
        }
    }
}
