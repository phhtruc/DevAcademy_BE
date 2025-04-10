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

    // Auth and User 1***
    EMAIL_INVALID(1000, "Email invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1001, "Password must be not blank", HttpStatus.BAD_REQUEST),
    PLATFORM_INVALID(1002, "Platform must be not null", HttpStatus.BAD_REQUEST),
    FULL_NAME_INVALID(1003, "Full name must be not null", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004, "User not found", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1004, "Email exists", HttpStatus.BAD_REQUEST),

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

    ;
    Integer code;
    String message;
    HttpStatusCode statusCode;
}
