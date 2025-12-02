package org.ek.portfoliobackend.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;


@Entity
public class Project {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    private LocalDate executionDate;

    private LocalDate creationDate;

    @JsonManagedReference // Stopper recursion
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public Project() {}


    public Project(Long id, String title, String description, WorkType workType, CustomerType customerType, LocalDate executionDate, LocalDate creationDate, List<Image> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.workType = workType;
        this.customerType = customerType;
        this.executionDate = executionDate;
        this.creationDate = creationDate;
        this.images = images;
    }


    public void addImage(Image image) {
        images.add(image);
        image.setProject(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setProject(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkType getServiceCategory() {
        return workType;
    }

    public void setServiceCategory(WorkType workType) {
        this.workType = workType;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }


}
