package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Log;
import com.mycompany.myapp.repository.LogRepository;
import com.mycompany.myapp.service.LogService;
import com.mycompany.myapp.service.dto.LogDTO;
import com.mycompany.myapp.service.mapper.LogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Log}.
 */
@Service
@Transactional
public class LogServiceImpl implements LogService {

    private static final Logger LOG = LoggerFactory.getLogger(LogServiceImpl.class);

    private final LogRepository logRepository;

    private final LogMapper logMapper;

    public LogServiceImpl(LogRepository logRepository, LogMapper logMapper) {
        this.logRepository = logRepository;
        this.logMapper = logMapper;
    }

    @Override
    public LogDTO save(LogDTO logDTO) {
        LOG.debug("Request to save Log : {}", logDTO);
        Log log = logMapper.toEntity(logDTO);
        log = logRepository.save(log);
        return logMapper.toDto(log);
    }

    @Override
    public LogDTO update(LogDTO logDTO) {
        LOG.debug("Request to update Log : {}", logDTO);
        Log log = logMapper.toEntity(logDTO);
        log = logRepository.save(log);
        return logMapper.toDto(log);
    }

    @Override
    public Optional<LogDTO> partialUpdate(LogDTO logDTO) {
        LOG.debug("Request to partially update Log : {}", logDTO);

        return logRepository
            .findById(logDTO.getId())
            .map(existingLog -> {
                logMapper.partialUpdate(existingLog, logDTO);

                return existingLog;
            })
            .map(logRepository::save)
            .map(logMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Logs");
        return logRepository.findAll(pageable).map(logMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LogDTO> findOne(Long id) {
        LOG.debug("Request to get Log : {}", id);
        return logRepository.findById(id).map(logMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Log : {}", id);
        logRepository.deleteById(id);
    }
}
