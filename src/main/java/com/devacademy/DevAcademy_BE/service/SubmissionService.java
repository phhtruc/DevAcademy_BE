package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmissionResponseDTO;
import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmitRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface SubmissionService {
    String submission(SubmitRequestDTO requestDTO, MultipartFile file, Authentication authentication)
            throws IOException, InterruptedException;

    List<SubmissionResponseDTO> submissionHistory(Long lessonId, Authentication authentication);
}
