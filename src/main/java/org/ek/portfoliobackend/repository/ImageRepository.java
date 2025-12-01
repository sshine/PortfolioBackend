package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.Image;
import org.ek.portfoliobackend.model.ImageType;
import org.ek.portfoliobackend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    //find all images by project
    List<Image> findByProject(Project project);

    //find all images by project id
    List<Image> findByProjectId(Long projectId);

    //Find all featured images
    List<Image> findByIsFeaturedTrue();

    //Find all images by type (BEFORE/AFTER)
    List<Image> findByImageType(ImageType imageType);

    //Find all featured images by project
    List<Image> findByProjectIdAndIsFeaturedTrue(Long projectId);
}
