package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.Project;
import org.ek.portfoliobackend.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    //find all projects by service category
    List<Project> findByServiceCategory(ServiceCategory serviceCategory);

    //find all projects by customer type
    List<Project> findByCustomerType(CustomerType customerType);

    //find all projects by service category and customer type
    List<Project> findByServiceCategoryAndCustomerType(ServiceCategory serviceCategory, CustomerType customerType);

    //find all projects by execution date between two dates
    List<Project> findByExecutionDateBetween(LocalDate startDate, LocalDate endDate);

    //find all projects ordered by creation date descending
    List<Project> findAllByOrderByCreationDateDesc();
}
