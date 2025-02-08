package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Amortization;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Amortization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AmortizationRepository extends JpaRepository<Amortization, Long> {}
