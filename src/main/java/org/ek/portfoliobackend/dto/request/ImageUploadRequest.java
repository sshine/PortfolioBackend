package org.ek.portfoliobackend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.ek.portfoliobackend.model.ImageType;

public class ImageUploadRequest {

    @NotNull(message = "The image requires an image type")
    private ImageType imageType;

    @JsonProperty(defaultValue = "false") // Bliver false i backend, hvis brugeren ikke angiver værdien
    private Boolean isFeatured;

    public ImageUploadRequest() {}

    public ImageUploadRequest(ImageType imageType, boolean isFeatured) {
        this.imageType = imageType;
        this.isFeatured = isFeatured;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
}
