package com.example.order_app.dto;

import lombok.Data;

import java.sql.Blob;

@Data
public class ImageDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;

}
