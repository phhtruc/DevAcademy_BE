package com.devacademy.DevAcademy_BE.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public interface AIService {
    String reviewCode(String content) throws IOException, InterruptedException;

}
