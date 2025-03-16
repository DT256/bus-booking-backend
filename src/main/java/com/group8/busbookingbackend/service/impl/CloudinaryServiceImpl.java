package com.group8.busbookingbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group8.busbookingbackend.service.ICloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements ICloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
                "asset_folder", "bus_booking/image",
                "resource_type", "image"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("url").toString(); // Trả về URL của file đã upload
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
                "asset_folder", "bus_booking/video",
                "resource_type", "video"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("url").toString(); // Trả về URL của file đã upload
    }




}
/*
Cấu hình folder trên Cloudinary:
Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
        ObjectUtils.asMap("folder", "my_folder"));


Resize hoặc nén hình ảnh:
Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
    ObjectUtils.asMap("transformation", new Transformation().width(500).height(500).crop("fit")));


Xóa file trên Cloudinary:
public String deleteFile(String publicId) throws IOException {
    Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    return result.get("result").toString(); // Trả về kết quả "ok" nếu xóa thành công
}

*/