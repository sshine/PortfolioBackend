package org.ek.portfoliobackend.model;


import jakarta.persistence.*;


@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    private boolean isFeatured;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Image() {}

    public Image(Long id, String url, ImageType imageType, boolean isFeatured, Project project) {
        this.id = id;
        this.url = url;
        this.imageType = imageType;
        this.isFeatured = isFeatured;
        this.project = project;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }


}
