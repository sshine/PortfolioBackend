package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.Image;
import org.ek.portfoliobackend.model.ImageType;
import org.ek.portfoliobackend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // Find all images by project
    List<Image> findByProject(Project project);

    // Find all images by project id
    List<Image> findByProjectId(Long projectId);

    // Find all featured images
    List<Image> findByIsFeaturedTrue();

    // Find all non-featured images
    List<Image> findByIsFeaturedFalse();

    // Find all images by type (BEFORE/AFTER)
    List<Image> findByImageType(ImageType imageType);

    // Find all featured images by project
    List<Image> findByProjectIdAndIsFeaturedTrue(Long projectId);

    // Find all non-featured images by project
    List<Image> findByProjectIdAndIsFeaturedFalse(Long projectId);

    // Find images by type and featured status
    List<Image> findByImageTypeAndIsFeatured(ImageType imageType, boolean isFeatured);

    // Find images by project and type
    List<Image> findByProjectIdAndImageType(Long projectId, ImageType imageType);
}