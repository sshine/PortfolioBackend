package org.ek.portfoliobackend.dto.response;


import org.ek.portfoliobackend.model.ImageType;


public class ImageResponse {

    private Long id;
    private String url;
    private ImageType imageType;
    private boolean isFeatured;

    public ImageResponse(){}

    public ImageResponse(Long id, String url, ImageType imageType, boolean isFeatured) {
        this.id = id;
        this.url = url;
        this.imageType = imageType;
        this.isFeatured = isFeatured;
    }

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

    public boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(boolean featured) {
        isFeatured = featured;
    }



}
