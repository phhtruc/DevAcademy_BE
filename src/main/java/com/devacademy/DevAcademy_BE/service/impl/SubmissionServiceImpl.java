package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmissionResponseDTO;
import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmitRequestDTO;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.entity.PromptEntity;
import com.devacademy.DevAcademy_BE.entity.SubmissionEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.SubmitStatus;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.SubmissionMapper;
import com.devacademy.DevAcademy_BE.repository.PromptRepository;
import com.devacademy.DevAcademy_BE.repository.SubmissionRepository;
import com.devacademy.DevAcademy_BE.service.AIService;
import com.devacademy.DevAcademy_BE.service.SubmissionService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {
    static String githubToken = Dotenv.configure().filename("local.env").load().get("GITHUB_TOKEN");
    static String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}/zipball/{branch}";
    RestTemplate restTemplate;
    AIService AIService;
    PromptRepository promptRepository;
    SubmissionRepository submissionRepository;
    SubmissionMapper submissionMapper;

    @Override
    public String submission(SubmitRequestDTO requestDTO, MultipartFile file,
                             Authentication authentication) throws IOException, InterruptedException {
        if (requestDTO.getGithubLink() != null && !requestDTO.getGithubLink().isEmpty()) {
            if (!requestDTO.getGithubLink().startsWith("https://github.com/")) {
                throw new ApiException(ErrorCode.GITHUB_NOT_FOUND);
            }
            GitHubRepoInfo repoInfo = parseGitHubUrl(requestDTO.getGithubLink());

            String zipUrl = GITHUB_API_URL
                    .replace("{owner}", repoInfo.owner())
                    .replace("{repo}", repoInfo.repo())
                    .replace("{branch}", repoInfo.branch());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github+json");

            RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(zipUrl));
            ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, String> javaFiles = extractTextFilesFromZip(new ByteArrayInputStream(response.getBody()));

                var resultReview = AIService.reviewCode(contentProcess(javaFiles, requestDTO));
                addSubmit(requestDTO.getGithubLink(), resultReview, authentication, requestDTO.getIdExercise());
                return resultReview;
            } else {
                throw new RuntimeException("Failed to download zip: " + response.getStatusCode());
            }
        } else if (file != null && !file.isEmpty()) {
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".zip")) {
                throw new ApiException(ErrorCode.INVALID_FILE_FORMAT);
            }

            Map<String, String> textFiles = extractTextFilesFromZip(file.getInputStream());

            var resultReview = AIService.reviewCode(contentProcess(textFiles, requestDTO));
            addSubmit(requestDTO.getGithubLink(), resultReview, authentication, requestDTO.getIdExercise());
            return resultReview;
        } else {
            throw new ApiException(ErrorCode.MISSING_SUBMISSION_DATA);
        }
    }

    @Override
    public List<SubmissionResponseDTO> submissionHistory(Long lessonId, Authentication auth) {
        UserEntity user = (UserEntity) auth.getPrincipal();

        return submissionRepository
                .findAllByUserIdAndLessonEntityIdOrderByCreatedDateDesc(user.getId(), lessonId).stream()
                .map(submissionMapper::toSubmissionResponseDTO)
                .collect(Collectors.toList());
    }

    private GitHubRepoInfo parseGitHubUrl(String url) {
        Pattern pattern = Pattern.compile("https://github.com/([^/]+)/([^/]+)(/tree/([^/]+))?");
        Matcher matcher = pattern.matcher(url);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GitHub URL: " + url);
        }

        String owner = matcher.group(1);
        String repo = matcher.group(2).replaceAll("\\.git$", "");
        String branch = matcher.group(4) != null ? matcher.group(4) : getDefaultBranch(owner, repo);

        return new GitHubRepoInfo(owner, repo, branch);
    }

    private record GitHubRepoInfo(String owner, String repo, String branch) {
    }

    private Map<String, String> extractTextFilesFromZip(InputStream zipStream) throws IOException {
        Map<String, String> textFiles = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipStream))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fullPath = entry.getName();
                String fileName = Paths.get(fullPath).getFileName().toString();

                if (entry.isDirectory() ||
                        shouldIgnoreDirectory(fullPath) ||
                        shouldIgnoreFile(fileName)) continue;

                String content = new String(readAllBytes(zis), StandardCharsets.UTF_8);
                textFiles.put(fileName, content);
            }
        }
        return textFiles;
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    private String getDefaultBranch(String owner, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s", owner, repo);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return (String) Objects.requireNonNull(response.getBody()).get("default_branch");
            }

        } catch (Exception e) {
            log.error("Error fetching default branch: {}", e.getMessage());
        }
        return "main";
    }

    private static final Set<String> IGNORE_DIRECTORIES = new HashSet<>(Arrays.asList(
            "node_modules", "target", "build", "dist", "out", ".git", ".svn",
            "__pycache__", ".pytest_cache", "venv", "env", "assets",
            "bin", "obj", "packages", ".gradle", ".idea", ".vscode", ".mvn"
    ));

    private static final Set<String> IGNORE_FILES = new HashSet<>(Arrays.asList(
            ".gitignore", ".gitkeep", ".dockerignore", "Dockerfile", "mvnw",
            "package-lock.json", "yarn.lock", "Gemfile.lock", "composer.lock", ".gitattributes"
    ));

    private static final Set<String> IGNORE_FILE_EXTENSIONS = Set.of(
            ".md", ".iml", ".env", ".yml", ".cmd", ".csproj", ".browserslistrc");

    private static final Set<String> IGNORE_BINARY_EXTENSIONS = Set.of(
            ".png", ".jpg", ".jpeg", ".gif", ".exe", ".class", ".jar", ".zip",
            ".tar", ".gz", ".pdf", ".ico"
    );

    private boolean shouldIgnoreDirectory(String path) {
        for (String ignoreDir : IGNORE_DIRECTORIES) {
            if (path.contains("/" + ignoreDir + "/")) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldIgnoreFile(String fileName) {
        return IGNORE_FILES.contains(fileName) ||
                IGNORE_FILE_EXTENSIONS.stream().anyMatch(fileName::endsWith) ||
                IGNORE_BINARY_EXTENSIONS.stream().anyMatch(fileName.toLowerCase()::endsWith);
    }

    private String contentProcess(Map<String, String> javaFiles, SubmitRequestDTO requestDTO) throws IOException {
        StringBuilder messageBuilder = new StringBuilder();

        String prompt = promptRepository.findPromptEntityByCourseEntityIdAndIsActive
                        (Long.parseLong(requestDTO.getIdCourse()), true)
                .map(PromptEntity::getContentStruct)
                .orElseThrow(() -> new ApiException(ErrorCode.PROMPT_NOT_FOUNT_ERROR));

        prompt = prompt.formatted(requestDTO.getExerciseTitle(), requestDTO.getLanguage());

        messageBuilder.append(prompt).append("\n\n");

        for (Map.Entry<String, String> entry : javaFiles.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            messageBuilder.append("File: ").append(fileName).append("\n");
            messageBuilder.append("```\n");
            messageBuilder.append(content).append("\n");
            messageBuilder.append("```\n\n");
        }

        return messageBuilder.toString();
    }

    private void addSubmit(String githubLink, String resultReview, Authentication authentication, String idExercise) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        SubmitStatus status;
        if (resultReview.contains("PASS")) {
            status = SubmitStatus.PASS;
        } else if (resultReview.contains("FAIL")) {
            status = SubmitStatus.FAIL;
        } else {
            status = SubmitStatus.PENDING;
        }
        var submit = SubmissionEntity.builder()
                .submissionLink(githubLink)
                .review(resultReview)
                .user(user)
                .isDeleted(false)
                .status(status)
                .lessonEntity(LessonEntity.builder().id(Long.parseLong(idExercise)).build())
                .build();
        submissionRepository.save(submit);
    }
}
