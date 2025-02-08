package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Alert;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Alert entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    @Query("select alert from Alert alert where alert.user.login = ?#{authentication.name}")
    List<Alert> findByUserIsCurrentUser();
}
