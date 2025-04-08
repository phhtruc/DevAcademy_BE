package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.service.UserService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
public class UserController {

    UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUser(@RequestParam(required = false, defaultValue = "1") int page,
                                     @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return JsonResponse.ok(userService.getAllUser(page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return JsonResponse.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRequestDTO request) {
        return JsonResponse.ok(userService.addUser(request));
    }

//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestParam(value = "avatar", required = false) MultipartFile multipartFile,
//                                        @ModelAttribute @Valid UserRequestDTO request) {
//        return JsonResponse.ok(userService.updateUserDTO(id, request));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return JsonResponse.deleted();
    }

}
