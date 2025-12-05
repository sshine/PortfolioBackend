package org.ek.portfoliobackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.WorkType;

import java.time.LocalDate;

public class CreateProjectRequest {

    @NotBlank(message = "The project requires a title")
    private String title;

    @NotBlank(message = "The project requires a description")
    private String description;

    @NotNull(message = "The project requires an execution date")
    private LocalDate executionDate;

    @NotNull(message = "The project requires a service category")
    private WorkType workType;

    @NotNull(message = "The project requires a customer type")
    private CustomerType customerType;

    public CreateProjectRequest() {}

    public CreateProjectRequest(String title, String description, LocalDate executionDate,
                                WorkType workType, CustomerType customerType) {
        this.title = title;
        this.description = description;
        this.executionDate = executionDate;
        this.workType = workType;
        this.customerType = customerType;
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
}

