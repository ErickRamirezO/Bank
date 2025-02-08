package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.LogAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Log;
import com.mycompany.myapp.repository.LogRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.LogDTO;
import com.mycompany.myapp.service.mapper.LogMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LogResourceIT {

    private static final String DEFAULT_EVENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_EVENT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVENT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLogMockMvc;

    private Log log;

    private Log insertedLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Log createEntity() {
        return new Log()
            .eventType(DEFAULT_EVENT_TYPE)
            .eventTime(DEFAULT_EVENT_TIME)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Log createUpdatedEntity() {
        return new Log()
            .eventType(UPDATED_EVENT_TYPE)
            .eventTime(UPDATED_EVENT_TIME)
            .ipAddress(UPDATED_IP_ADDRESS)
            .description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        log = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedLog != null) {
            logRepository.delete(insertedLog);
            insertedLog = null;
        }
    }

    @Test
    @Transactional
    void createLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);
        var returnedLogDTO = om.readValue(
            restLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(logDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LogDTO.class
        );

        // Validate the Log in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLog = logMapper.toEntity(returnedLogDTO);
        assertLogUpdatableFieldsEquals(returnedLog, getPersistedLog(returnedLog));

        insertedLog = returnedLog;
    }

    @Test
    @Transactional
    void createLogWithExistingId() throws Exception {
        // Create the Log with an existing ID
        log.setId(1L);
        LogDTO logDTO = logMapper.toDto(log);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(logDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEventTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        log.setEventType(null);

        // Create the Log, which fails.
        LogDTO logDTO = logMapper.toDto(log);

        restLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(logDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLogs() throws Exception {
        // Initialize the database
        insertedLog = logRepository.saveAndFlush(log);

        // Get all the logList
        restLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(log.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventType").value(hasItem(DEFAULT_EVENT_TYPE)))
            .andExpect(jsonPath("$.[*].eventTime").value(hasItem(DEFAULT_EVENT_TIME.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getLog() throws Exception {
        // Initialize the database
        insertedLog = logRepository.saveAndFlush(log);

        // Get the log
        restLogMockMvc
            .perform(get(ENTITY_API_URL_ID, log.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(log.getId().intValue()))
            .andExpect(jsonPath("$.eventType").value(DEFAULT_EVENT_TYPE))
            .andExpect(jsonPath("$.eventTime").value(DEFAULT_EVENT_TIME.toString()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingLog() throws Exception {
        // Get the log
        restLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLog() throws Exception {
        // Initialize the database
        insertedLog = logRepository.saveAndFlush(log);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the log
        Log updatedLog = logRepository.findById(log.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLog are not directly saved in db
        em.detach(updatedLog);
        updatedLog
            .eventType(UPDATED_EVENT_TYPE)
            .eventTime(UPDATED_EVENT_TIME)
            .ipAddress(UPDATED_IP_ADDRESS)
            .description(UPDATED_DESCRIPTION);
        LogDTO logDTO = logMapper.toDto(updatedLog);

        restLogMockMvc
            .perform(put(ENTITY_API_URL_ID, logDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(logDTO)))
            .andExpect(status().isOk());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLogToMatchAllProperties(updatedLog);
    }

    @Test
    @Transactional
    void putNonExistingLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        log.setId(longCount.incrementAndGet());

        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogMockMvc
            .perform(put(ENTITY_API_URL_ID, logDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(logDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        log.setId(longCount.incrementAndGet());

        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(logDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        log.setId(longCount.incrementAndGet());

        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(logDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLogWithPatch() throws Exception {
        // Initialize the database
        insertedLog = logRepository.saveAndFlush(log);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the log using partial update
        Log partialUpdatedLog = new Log();
        partialUpdatedLog.setId(log.getId());

        partialUpdatedLog
            .eventType(UPDATED_EVENT_TYPE)
            .eventTime(UPDATED_EVENT_TIME)
            .ipAddress(UPDATED_IP_ADDRESS)
            .description(UPDATED_DESCRIPTION);

        restLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLog))
            )
            .andExpect(status().isOk());

        // Validate the Log in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLogUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLog, log), getPersistedLog(log));
    }

    @Test
    @Transactional
    void fullUpdateLogWithPatch() throws Exception {
        // Initialize the database
        insertedLog = logRepository.saveAndFlush(log);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the log using partial update
        Log partialUpdatedLog = new Log();
        partialUpdatedLog.setId(log.getId());

        partialUpdatedLog
            .eventType(UPDATED_EVENT_TYPE)
            .eventTime(UPDATED_EVENT_TIME)
            .ipAddress(UPDATED_IP_ADDRESS)
            .description(UPDATED_DESCRIPTION);

        restLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLog))
            )
            .andExpect(status().isOk());

        // Validate the Log in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLogUpdatableFieldsEquals(partialUpdatedLog, getPersistedLog(partialUpdatedLog));
    }

    @Test
    @Transactional
    void patchNonExistingLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        log.setId(longCount.incrementAndGet());

        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, logDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(logDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        log.setId(longCount.incrementAndGet());

        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(logDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        log.setId(longCount.incrementAndGet());

        // Create the Log
        LogDTO logDTO = logMapper.toDto(log);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(logDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Log in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLog() throws Exception {
        // Initialize the database
        insertedLog = logRepository.saveAndFlush(log);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the log
        restLogMockMvc.perform(delete(ENTITY_API_URL_ID, log.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return logRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Log getPersistedLog(Log log) {
        return logRepository.findById(log.getId()).orElseThrow();
    }

    protected void assertPersistedLogToMatchAllProperties(Log expectedLog) {
        assertLogAllPropertiesEquals(expectedLog, getPersistedLog(expectedLog));
    }

    protected void assertPersistedLogToMatchUpdatableProperties(Log expectedLog) {
        assertLogAllUpdatablePropertiesEquals(expectedLog, getPersistedLog(expectedLog));
    }
}
