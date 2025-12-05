package org.ek.portfoliobackend.dto.response;


import java.time.LocalDate;
import java.util.List;


import org.ek.portfoliobackend.model.WorkType;
import org.ek.portfoliobackend.model.CustomerType;


public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate executionDate;
    private LocalDate creationDate;
    private WorkType workType;
    private CustomerType customerType;
    private List<ImageResponse> images;

    public ProjectResponse(Long id, String title, String description, LocalDate executionDate, LocalDate creationDate, WorkType workType, CustomerType customerType, List<ImageResponse> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.executionDate = executionDate;
        this.creationDate = creationDate;
        this.workType = workType;
        this.customerType = customerType;
        this.images = images;
    }
    public ProjectResponse() {}


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

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public List<ImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }



}
