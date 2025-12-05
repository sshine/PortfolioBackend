package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.Project;
import org.ek.portfoliobackend.model.WorkType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find all projects by service category
    List<Project> findByWorkType(WorkType workType);

    // Find all projects by customer type
    List<Project> findByCustomerType(CustomerType customerType);

    // Find all projects by service category and customer type
    List<Project> findByWorkTypeAndCustomerType(WorkType workType, CustomerType customerType);

    // Find all projects by execution date between two dates - TODO: bliver ikke brugt, slet?
    List<Project> findByExecutionDateBetween(LocalDate startDate, LocalDate endDate);

    // Find all projects ordered by creation date descending (newest first)
    List<Project> findAllByOrderByCreationDateDesc();

    // Find all projects ordered by creation date ascending (oldest first)
    List<Project> findAllByOrderByCreationDateAsc();

    // Find projects by service category ordered by creation date descending - TODO: bliver ikke brugt, slet?
    List<Project> findByWorkTypeOrderByCreationDateDesc(WorkType workType);

    // Find projects by customer type ordered by creation date descending - TODO: bliver ikke brugt, slet?
    List<Project> findByCustomerTypeOrderByCreationDateDesc(CustomerType customerType);

    // Find projects by work type with dynamic sorting in any direction
    List<Project> findByWorkType(WorkType workType, Sort sort);

    // Find projects by customer type with dynamic sorting
    List<Project> findByCustomerType(CustomerType customerType, Sort sort);

    // Find projects by both workType and customerType with dynamic sorting
    List<Project> findByWorkTypeAndCustomerType(WorkType workType, CustomerType customerType, Sort sort);
}