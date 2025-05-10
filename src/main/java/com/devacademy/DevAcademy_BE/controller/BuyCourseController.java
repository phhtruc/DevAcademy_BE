package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.service.BuyCourseService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/buy_course")
public class BuyCourseController {

    BuyCourseService buyCourseService;

    @PostMapping
    public ResponseEntity<?> buyCourse (@RequestParam UUID idUser, @RequestParam long idCourse) throws MessagingException {
        return JsonResponse.ok(buyCourseService.buyCourse(idUser, idCourse));
    }

}
