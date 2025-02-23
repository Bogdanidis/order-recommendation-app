package com.example.order_app.controller;


import com.example.order_app.exception.InvalidImageException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Image;
import com.example.order_app.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ImageController {
    private final IImageService imageService;



    /**
     * Retrieves an image by its ID and returns it as a byte array.
     *
     * @param imageId The ID of the image to retrieve. This is a path variable in the URL.
     * @return A ResponseEntity containing:
     *         - The image data as a byte array in the response body
     *         - The appropriate content type header based on the image's file type
     *         - HTTP status 200 (OK) if the image is found and successfully retrieved
     *         - HTTP status 404 (Not Found) if no image exists with the given ID
     *         - HTTP status 500 (Internal Server Error) if there's an error reading the image data
     */
    @GetMapping("images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .body(image.getImage().getBytes(1, (int) image.getImage().length()));
        } catch (SQLException e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ResourceNotFoundException e) {
            // Log the exception details here
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles the upload of images for a product.
     *
     * @param productId ID of the product
     * @param files List of image files to upload
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL to the product details page
     */
    @PostMapping("products/{productId}/upload-images")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String uploadProductImages(@PathVariable Long productId,
                                      @RequestParam("files") List<MultipartFile> files,
                                      RedirectAttributes redirectAttributes) {
        try {
            imageService.saveImages(files, productId);
            redirectAttributes.addFlashAttribute("success", "Images uploaded successfully");
        } catch (InvalidImageException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid image: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Product not found: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while uploading images: " + e.getMessage());
        }
        return "redirect:/products/" + productId;
    }

    /**
     * Handles the deletion of a product image.
     *
     * @param productId ID of the product
     * @param imageId ID of the image to delete
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL to the product details page
     */
    @DeleteMapping("products/{productId}/delete-image/{imageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProductImage(@PathVariable Long productId,
                                     @PathVariable Long imageId,
                                     RedirectAttributes redirectAttributes) {
        try {
            imageService.deleteImageById(imageId);
            redirectAttributes.addFlashAttribute("success", "Image deactivated successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Image not found: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deactivating the image: " + e.getMessage());
        }
        return "redirect:/products/" + productId;
    }
}