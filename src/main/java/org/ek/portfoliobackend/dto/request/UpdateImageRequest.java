package org.ek.portfoliobackend.dto.request;



import org.ek.portfoliobackend.model.ImageType;


public class UpdateImageRequest {
    private Long id;
    private String url;
    private ImageType imageType;
    private Boolean isFeatured;

    public UpdateImageRequest(Long id, String url, ImageType imageType, Boolean isFeatured) {
        this.id = id;
        this.url = url;
        this.imageType = imageType;
        this.isFeatured = isFeatured;
    }

   public UpdateImageRequest() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean featured) {
        isFeatured = featured;
    }


}
