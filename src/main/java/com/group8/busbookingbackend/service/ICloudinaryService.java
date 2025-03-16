package com.group8.busbookingbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    public String uploadVideo(MultipartFile file) throws IOException;
    public String uploadImage(MultipartFile file) throws IOException;

}
