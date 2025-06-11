package com.devacademy.DevAcademy_BE.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    GET_SUCCESSFUL(1010, "Get successful", HttpStatus.OK),
    ADD_SUCCESSFUL(1011, "Add successful", HttpStatus.OK),
    DELETE_SUCCESSFUL(1012, "Delete successful", HttpStatus.OK),
    UPDATE_SUCCESSFUL(1013, "Update successful", HttpStatus.OK),
    INVALID_DATA(1014, "Invalid data", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(1020, "Review not found", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1021, "Invalid or expired token", HttpStatus.BAD_REQUEST),

    // Auth and User 1***
    EMAIL_INVALID(1000, "Email invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1001, "Password must be not blank", HttpStatus.BAD_REQUEST),
    PLATFORM_INVALID(1002, "Platform must be not null", HttpStatus.BAD_REQUEST),
    FULL_NAME_INVALID(1003, "Full name must be not null", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1004, "Email exists", HttpStatus.BAD_REQUEST),
    ROLE_INVALID_TYPE(1005, "Email exists", HttpStatus.BAD_REQUEST),

    //Role 2***
    ROLE_NAME_NOT_FOUND(2000, "Role name not found", HttpStatus.BAD_REQUEST),

    // error code course: 1700**
    COURSE_NOT_EXISTED(170001, "Course not existed", HttpStatus.NOT_FOUND),
    COURSE_NAME_INVALID(170002, "Course name must be not blank", HttpStatus.NOT_FOUND),
    COURSE_PRICE_INVALID(170003, "Course price must be not blank", HttpStatus.NOT_FOUND),
    COURSE_THUMBNAIL_URL_INVALID(170004, "Course thumbnail url must be not blank", HttpStatus.NOT_FOUND),
    COURSE_DESCRIPTION_INVALID(170006, "Course description must be not blank", HttpStatus.NOT_FOUND),
    COURSE_UNIT_INVALID(170007, "Course currency unit must be not blank", HttpStatus.NOT_FOUND),
    COURSE_IS_PUBLIC_INVALID(170009, "Course is public must be not blank", HttpStatus.NOT_FOUND),
    COURSE_IS_PUBLIC_INVALID_TYPE(170012, "The course is public must be false or true", HttpStatus.NOT_FOUND),
    COURSE_TECH_STACK_INVALID(170013, "Course is tech stack must be not null", HttpStatus.NOT_FOUND),
    TECH_STACK_NOT_EXISTED(170014, "Tech stack not exists", HttpStatus.NOT_FOUND),
    DURATION_INVALID(170015, "Duration must be not blank", HttpStatus.NOT_FOUND),

    // Error code for chapter: 1800**
    CHAPTER_NOT_EXISTED(180001, "Chapter not existed", HttpStatus.NOT_FOUND),
    CHAPTER_NAME_INVALID(180002, "Chapter name must be not blank", HttpStatus.BAD_REQUEST),
    CHAPTER_ORDER_INVALID(180003, "Chapter chapter_order must be a valid number", HttpStatus.BAD_REQUEST),
    CHAPTER_IS_PUBLIC_INVALID(180004, "Chapter public status must be not blank", HttpStatus.BAD_REQUEST),
    CHAPTER_IS_DELETED_INVALID(180005, "Chapter deletion status must be false or true", HttpStatus.BAD_REQUEST),
    CHAPTER_COURSE_ID_INVALID(180006, "Chapter must be associated with a valid course", HttpStatus.BAD_REQUEST),
    CHAPTER_IS_PUBLIC_INVALID_TYPE(180007, "Chapter public status must be false or true", HttpStatus.BAD_REQUEST),
    CHAPTER_NOT_FOUND(180008, "Chapter not found", HttpStatus.NOT_FOUND),

    // error code lesson: 1900**
    LESSON_NOT_EXISTED(190001, "Lesson not existed", HttpStatus.NOT_FOUND),
    LESSON_TITLE_INVALID(190002, "Lesson title must be not blank", HttpStatus.NOT_FOUND),
    LESSON_TYPE_INVALID(190003, "Lesson type must be not blank", HttpStatus.NOT_FOUND),
    LESSON_ORDER_INVALID(190004, "Lesson order url must be not blank", HttpStatus.NOT_FOUND),
    LESSON_CONTENT_INVALID(190005, "Lesson content must be not blank", HttpStatus.NOT_FOUND),
    LESSON_VIDEO_URL_INVALID(190006, "Lesson video url must be not blank", HttpStatus.NOT_FOUND),
    COURSE_CONTENT_REFER_INVALID(190007, "Lesson content refer unit must be not blank", HttpStatus.NOT_FOUND),
    COURSE_TYPE_INVALID_TYPE(190008, "The Lesson type must be READINGS, LECTURES or EXERCISES", HttpStatus.NOT_FOUND),
    CHAPTER_ID_INVALID(190009, "Chapter id must be not blank", HttpStatus.NOT_FOUND),

    // course has tech stack 2000**
    COURSE_TECH_STACK_NOT_FOUNT(200000, "Course has tech stack not found", HttpStatus.NOT_FOUND),
    TECH_STACK_NAME_INVALID(200001, "Tech stack name must be not blank", HttpStatus.NOT_FOUND),

    // category 2100**
    CATEGORY_NOT_FOUNT(210000, "Category not found", HttpStatus.NOT_FOUND),

    // category has course 2200**
    CATEGORY_HAS_COURSE_NOT_FOUNT(210000, "Category has course not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_INVALID(21001, "Category name must be not blank", HttpStatus.NOT_FOUND),

    // prompt 2300**
    PROMPT_NOT_FOUNT(230000, "Prompt not found", HttpStatus.NOT_FOUND),
    PROMPT_NOT_FOUNT_ERROR(230001, "Khoá học chưa được cấu hình prompt để nộp bài tập. Thử lại sau!", HttpStatus.NOT_FOUND),

    // comment 2400**
    CONTENT_INVALID(240000, "Content cannot be blank", HttpStatus.NOT_FOUND),
    COMMENT_NOT_EXISTED(240001, "Comment not existed", HttpStatus.NOT_FOUND),
    DELETE_ERROR(240001, "You are not authorized to delete this comment", HttpStatus.NOT_FOUND),

    // payment 2500**
    INVALID_PAYMENT_DATA(250000, "Invalid payment data", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED(250001, "Payment failed", HttpStatus.BAD_REQUEST),

    // error code github : 2600*
    GITHUB_LINK(26000, "Github link must be not blank", HttpStatus.BAD_REQUEST),
    GITHUB_NOT_FOUND(26001, "Đường dẫn github không đúng định dạng", HttpStatus.BAD_REQUEST),
    GITHUB_API_ERROR(26002, "Error calling GitHub API", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_FORMAT(26003, "Invalid file format", HttpStatus.BAD_REQUEST),
    MISSING_SUBMISSION_DATA(26004, "Missing submission data", HttpStatus.BAD_REQUEST),

    // submission : 2700*
    SUBMISSION_NOT_FOUND(27000, "Submission not found", HttpStatus.BAD_REQUEST),

    ;

    Integer code;
    String message;
    HttpStatusCode statusCode;
}
