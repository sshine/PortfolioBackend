package org.ek.portfoliobackend.service;

import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.response.ImageResponse;
import org.ek.portfoliobackend.model.ImageType;

import java.util.List;

public interface ImageService {

    //upload and assign image to project
    ImageResponse uploadImage(Long projectId, String url, ImageUploadRequest request);

    //get image by id
    ImageResponse getImageById(Long id);

    //get all images for specific project
    List<ImageResponse> getImagesByProjectId(Long projectId);

    //delete image by id
    void deleteImage(Long id);

    //set image as featured
    ImageResponse setFeatured(Long id, boolean isFeatured);

    //get all featured images
    List<ImageResponse> getFeaturedImages();

    //Get featured images for specific project
    List<ImageResponse> getFeaturedImagesByProjectId(Long projectId);

    //get images by type (BEFORE/AFTER)
    List<ImageResponse> getImagesByType(ImageType imageType);
}
