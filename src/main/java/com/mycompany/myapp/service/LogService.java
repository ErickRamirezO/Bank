package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.LogDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Log}.
 */
public interface LogService {
    /**
     * Save a log.
     *
     * @param logDTO the entity to save.
     * @return the persisted entity.
     */
    LogDTO save(LogDTO logDTO);

    /**
     * Updates a log.
     *
     * @param logDTO the entity to update.
     * @return the persisted entity.
     */
    LogDTO update(LogDTO logDTO);

    /**
     * Partially updates a log.
     *
     * @param logDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LogDTO> partialUpdate(LogDTO logDTO);

    /**
     * Get all the logs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LogDTO> findAll(Pageable pageable);

    /**
     * Get the "id" log.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LogDTO> findOne(Long id);

    /**
     * Delete the "id" log.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
