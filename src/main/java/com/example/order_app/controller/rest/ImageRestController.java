package com.example.order_app.controller.rest;

import com.example.order_app.dto.ImageDto;
import com.example.order_app.exception.InvalidImageException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Image;
import com.example.order_app.response.RestResponse;
import com.example.order_app.service.image.IImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
@Tag(name = "Images", description = "Endpoints for managing product images")
public class ImageRestController {
    private final IImageService imageService;


    /**
     * Get image by ID
     */
    @GetMapping("/{imageId}")
    @Operation(summary = "Get image by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<?> getImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            byte[] imageData = image.getImage().getBytes(1, (int) image.getImage().length());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + image.getFileName() + "\"")
                    .body(imageData);
        } catch (SQLException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error retrieving image", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Download image
     */
    @GetMapping("/{imageId}/download")
    @Operation(summary = "Download image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<?> downloadImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            ByteArrayResource resource = new ByteArrayResource(
                    image.getImage().getBytes(1, (int) image.getImage().length())
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + image.getFileName() + "\"")
                    .body(resource);
        } catch (SQLException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error downloading image", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Upload images for a product (Admin only)
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Upload images for product", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Images uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image format")
    })
    public ResponseEntity<RestResponse<?>> uploadImages(
            @RequestParam List<MultipartFile> files,
            @RequestParam Long productId) {
        try {
            List<ImageDto> imageDtos = imageService.saveImages(files, productId);
            return ResponseEntity.ok(new RestResponse<>("Images uploaded successfully", imageDtos));
        } catch (InvalidImageException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new RestResponse<>("Invalid image: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error uploading images: " + e.getMessage(), null));
        }
    }

    /**
     * Update image (Admin only)
     */
    @PutMapping("/{imageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update image", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<RestResponse<?>> updateImage(
            @PathVariable Long imageId,
            @RequestParam MultipartFile file) {
        try {
            imageService.updateImage(file, imageId);
            return ResponseEntity.ok(new RestResponse<>("Image updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        } catch (InvalidImageException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new RestResponse<>("Invalid image: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error updating image: " + e.getMessage(), null));
        }
    }

    /**
     * Delete image (Admin only)
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete image", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<RestResponse<?>> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImageById(imageId);
            return ResponseEntity.ok(new RestResponse<>("Image deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}
