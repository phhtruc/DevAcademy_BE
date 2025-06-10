package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmitRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface SubmissionService {

//    void addSubmit(String linkGithub, String resultReview, String id, String idAss);
//
//    PageResponse<?> getAllReviews(int page, int pageSize, UUID userId, long assignmentId);
//
//    PageResponse<?> getAllSubmitByStatus(int page, int pageSize, UUID userId, Long assignmentId, String status);
//
//    ReviewResponseDTO getReviewById(long assignment, UUID id);

    String submission(SubmitRequestDTO requestDTO, MultipartFile file);
}
