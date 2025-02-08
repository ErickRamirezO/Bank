package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Log;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Log entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    @Query("select log from Log log where log.user.login = ?#{authentication.name}")
    List<Log> findByUserIsCurrentUser();
}
