package org.ek.portfoliobackend.dto.request;



import org.ek.portfoliobackend.model.WorkType;
import org.ek.portfoliobackend.model.CustomerType;
import java.time.LocalDate;


public class UpdateProjectRequest {
    private String title;
    private String description;

    public UpdateProjectRequest(String title, String description, WorkType workType, CustomerType customerType, LocalDate executionDate) {
        this.title = title;
        this.description = description;
        this.workType = workType;
        this.customerType = customerType;
        this.executionDate = executionDate;
    }

    public UpdateProjectRequest() {}

    private WorkType workType; // TODO: Kan vi ændre klassen til at hedde JobTypeCategory i stedet?
    private CustomerType customerType;
    private LocalDate executionDate;



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

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }



}
