package org.ek.portfoliobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import org.ek.portfoliobackend.model.ServiceCategory;
import org.ek.portfoliobackend.model.CustomerType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate executionDate;
    private LocalDate creationDate;
    private ServiceCategory serviceCategory;
    private CustomerType customerType;
    private List<ImageResponse> images;

}
