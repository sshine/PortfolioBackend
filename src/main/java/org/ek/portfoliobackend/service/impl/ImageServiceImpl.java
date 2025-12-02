package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.response.ImageResponse;
import org.ek.portfoliobackend.model.ImageType;
import org.ek.portfoliobackend.repository.ImageRepository;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.ek.portfoliobackend.service.ImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProjectRepository projectRepository;

    public ImageServiceImpl(ImageRepository imageRepository, ProjectRepository projectRepository) {
        this.imageRepository = imageRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public ImageResponse uploadImage(Long projectId, String url, ImageUploadRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ImageResponse getImageById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ImageResponse> getImagesByProjectId(Long projectId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteImage(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ImageResponse setFeatured(Long id, boolean isFeatured) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ImageResponse> getFeaturedImages() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ImageResponse> getFeaturedImagesByProjectId(Long projectId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ImageResponse> getImagesByType(ImageType imageType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
