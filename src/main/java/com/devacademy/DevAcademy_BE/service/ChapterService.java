package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.OrderDTO;
import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterRequestDTO;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterResponseDTO;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterSearchDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChapterService {

    ChapterResponseDTO getChapterById(Long id);

    ChapterResponseDTO addChapter(ChapterRequestDTO request);

    ChapterResponseDTO updateChapter(Long id, ChapterRequestDTO request);

    void deleteChapter(Long id);

    void updateOrder(List<OrderDTO> orderDTOS);

    PageResponse<?> getChapterByIdCourse(int page, int pageSize, Long id);

    PageResponse<?> searchChapters(ChapterSearchDTO searchDTO, int page, int pageSize);

}
