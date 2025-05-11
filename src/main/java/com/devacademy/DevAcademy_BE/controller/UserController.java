package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.userDTO.AdminRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserSearchDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserUpdateRequestDTO;
import com.devacademy.DevAcademy_BE.service.CourseService;
import com.devacademy.DevAcademy_BE.service.UserService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
public class UserController {
    CourseService courseService;
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUser(@RequestParam(required = false, defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(userService.getAllUser(page, pageSize));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return JsonResponse.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<?> addUser(@ModelAttribute @Valid AdminRequestDTO request,
                                     @RequestParam(required = false) MultipartFile avatar) throws IOException, MessagingException {
        return JsonResponse.ok(userService.addUser(request, avatar));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getCoursesByIdUser(@RequestParam(required = false, defaultValue = "1") int page,
                                                @RequestParam(required = false, defaultValue = "100") int pageSize,
                                                @PathVariable UUID id) {
        return JsonResponse.ok(courseService.getCoursesByIdUser(page, pageSize, id));
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id,
                                        @RequestParam(value = "avatar", required = false) MultipartFile multipartFile,
                                        @ModelAttribute @Valid UserUpdateRequestDTO request) throws IOException {
        return JsonResponse.ok(userService.updateUserDTO(id, request, multipartFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return JsonResponse.deleted();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> setActive(@PathVariable UUID id) {
        return JsonResponse.ok(userService.setActive(id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(UserSearchDTO searchDTO,
                                        @RequestParam(required = false, defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(userService.searchUser(searchDTO, page, pageSize));
    }

}
