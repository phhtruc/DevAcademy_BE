package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.BuyCourseResponseDTO;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.BuyCourseService;
import com.devacademy.DevAcademy_BE.service.CourseService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuyCourseServiceImpl implements BuyCourseService {

    UserRepository userRepository;
    GmailServiceImpl gmailService;
    CourseService courseService;

    @Override
    public BuyCourseResponseDTO buyCourse(UUID id, long id_course) throws MessagingException {
//        UserEntity user = userRepository.findUserById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        List<TeacherResponseDTO> teacherResponses = teacherService.getTeacherByCourseId(id_course);
//
//        Object course = courseService.getCourseById(id_course);
//
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> courseMap = mapper.convertValue(course, Map.class);
//
//        String courseName = (String) ((Map<String, Object>) courseMap.get("data")).get("name");
//
//        List<String> teacherNames = new ArrayList<>();
//        List<String> teacherEmails = new ArrayList<>();
//
//        for (TeacherResponseDTO teacher : teacherResponses) {
//            teacherNames.add(teacher.getName());
//            teacherEmails.add(teacher.getEmail());
//        }
//
//        if (studenCourseRepository.existUserIdAndIdCourse(id, id_course)) {
//            StudentCourseEntity studentCourseEntity = studenCourseRepository.findByUserEntityIdAndIdCourse(id, id_course);
//            gmailService.buyCourseMail(user.getFullName(), teacherNames, courseName, teacherEmails, user.getEmail());
//            return buyCourseMapper.toBuyCourseResponeDTO(studenCourseRepository.save(StudentCourseEntity.builder()
//                    .id(studentCourseEntity.getId())
//                    .idCourse(id_course)
//                    .userEntity(user)
//                    .status(StudentCourseStatus.PAID)
//                    .isDeleted(false)
//                    .build()));
//        } else {
//            gmailService.buyCourseMail(user.getFullName(), teacherNames, courseName, teacherEmails, user.getEmail());
//            return buyCourseMapper.toBuyCourseResponeDTO(studenCourseRepository.save(StudentCourseEntity.builder()
//                    .idCourse(id_course)
//                    .userEntity(user)
//                    .status(StudentCourseStatus.PAID)
//                    .isDeleted(false)
//                    .build()));
//        }
        return null;
    }

}
