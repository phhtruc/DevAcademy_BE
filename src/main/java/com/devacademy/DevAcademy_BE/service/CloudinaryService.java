package com.devacademy.DevAcademy_BE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        // Tải ảnh lên Cloudinary và lấy URL
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url"); // Lấy đường dẫn ảnh từ kết quả
    }

    public Map uploadFile(File file) throws IOException {
        return cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
    }

    public Map uploadFile(Byte file) throws IOException {
        return cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
    }
}
