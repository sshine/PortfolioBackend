package org.ek.portfoliobackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.ek.portfoliobackend.model.ServiceCategory;
import org.ek.portfoliobackend.model.CustomerType;
import java.time.LocalDate;

@Data // indeholder getter/setter + toString(), equals(), hashCode() (Sidste to er måske ikke nødvendige, men de er der)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectRequest {
    private String title;
    private String description;
    private ServiceCategory serviceCategory; // TODO: Kan vi ændre klassen til at hedde JobTypeCategory i stedet?
    private CustomerType customerType;
    private LocalDate executionDate;

}
