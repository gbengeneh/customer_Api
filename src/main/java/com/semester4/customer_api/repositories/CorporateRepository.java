package com.semester4.customer_api.repositories;

import com.semester4.customer_api.models.Corporate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorporateRepository extends JpaRepository<Corporate, Long> {
}
