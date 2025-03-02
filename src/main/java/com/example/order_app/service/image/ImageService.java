package com.example.order_app.service.image;

import com.example.order_app.dto.ImageDto;
import com.example.order_app.exception.InvalidImageException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Image;
import com.example.order_app.model.Product;
import com.example.order_app.repository.ImageRepository;
import com.example.order_app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final IProductService productService;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Add image dimension constraints
    private static final int MAX_IMAGE_WIDTH = 1200;
    private static final int MAX_IMAGE_HEIGHT = 1200;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Image not found with id " + id));
    }

    @Override
    @Transactional
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(
                        product -> imageRepository.softDelete(id, LocalDateTime.now()),
                        () -> { throw new ResourceNotFoundException("Image not found with id " + id); }
                );
    }

    @Override
    @Transactional
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file : files) {
            try{
                validateImage(file);

                // Resize image if necessary
                byte[] optimizedImageBytes = resizeImageIfNeeded(file.getBytes());

                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl="/order-api/v1/images/image/download/";
                String downloadUrl = buildDownloadUrl + image.getId();
                image.setDownloadUrl(downloadUrl);

                Image savedImage=imageRepository.save(image);

                //update the correct id after we have saved

                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                //added 15/10
                imageDto.setFileType(savedImage.getFileType());
                //
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDto.add(imageDto);

            } catch (IOException|SQLException e){
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    private void validateImage(MultipartFile file) throws InvalidImageException {
        if (file.isEmpty()) {
            throw new InvalidImageException("Uploaded file is empty");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidImageException("Invalid file type. Allowed types are JPEG, PNG, and GIF");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException("File size exceeds the maximum limit of 5MB");
        }
    }

    private byte[] resizeImageIfNeeded(byte[] imageData) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));

        if (img.getWidth() <= MAX_IMAGE_WIDTH && img.getHeight() <= MAX_IMAGE_HEIGHT) {
            return imageData; // No resize needed
        }

        // Calculate new dimensions maintaining aspect ratio
        int newWidth, newHeight;
        if (img.getWidth() > img.getHeight()) {
            newWidth = MAX_IMAGE_WIDTH;
            newHeight = (int) (img.getHeight() * ((double) MAX_IMAGE_WIDTH / img.getWidth()));
        } else {
            newHeight = MAX_IMAGE_HEIGHT;
            newWidth = (int) (img.getWidth() * ((double) MAX_IMAGE_HEIGHT / img.getHeight()));
        }

        // Resize image
        BufferedImage resized = new BufferedImage(newWidth, newHeight, img.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // Convert back to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", baos);
        return baos.toByteArray();
    }

    @Override
    @Transactional
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
