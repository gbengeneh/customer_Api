package com.semester4.customer_api.repositories;

import com.semester4.customer_api.models.Individual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualRepository extends JpaRepository<Individual, Long> {
}
