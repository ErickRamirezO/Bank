package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AlertAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Alert;
import com.mycompany.myapp.repository.AlertRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.AlertDTO;
import com.mycompany.myapp.service.mapper.AlertMapper;
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
 * Integration tests for the {@link AlertResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlertResourceIT {

    private static final String DEFAULT_ALERT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ALERT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_ALERT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ALERT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_EMAIL_SENT = false;
    private static final Boolean UPDATED_EMAIL_SENT = true;

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/alerts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertMapper alertMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlertMockMvc;

    private Alert alert;

    private Alert insertedAlert;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alert createEntity() {
        return new Alert()
            .alertType(DEFAULT_ALERT_TYPE)
            .alertTime(DEFAULT_ALERT_TIME)
            .emailSent(DEFAULT_EMAIL_SENT)
            .details(DEFAULT_DETAILS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Alert createUpdatedEntity() {
        return new Alert()
            .alertType(UPDATED_ALERT_TYPE)
            .alertTime(UPDATED_ALERT_TIME)
            .emailSent(UPDATED_EMAIL_SENT)
            .details(UPDATED_DETAILS);
    }

    @BeforeEach
    public void initTest() {
        alert = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAlert != null) {
            alertRepository.delete(insertedAlert);
            insertedAlert = null;
        }
    }

    @Test
    @Transactional
    void createAlert() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);
        var returnedAlertDTO = om.readValue(
            restAlertMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AlertDTO.class
        );

        // Validate the Alert in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAlert = alertMapper.toEntity(returnedAlertDTO);
        assertAlertUpdatableFieldsEquals(returnedAlert, getPersistedAlert(returnedAlert));

        insertedAlert = returnedAlert;
    }

    @Test
    @Transactional
    void createAlertWithExistingId() throws Exception {
        // Create the Alert with an existing ID
        alert.setId(1L);
        AlertDTO alertDTO = alertMapper.toDto(alert);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAlertTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alert.setAlertType(null);

        // Create the Alert, which fails.
        AlertDTO alertDTO = alertMapper.toDto(alert);

        restAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailSentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        alert.setEmailSent(null);

        // Create the Alert, which fails.
        AlertDTO alertDTO = alertMapper.toDto(alert);

        restAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAlerts() throws Exception {
        // Initialize the database
        insertedAlert = alertRepository.saveAndFlush(alert);

        // Get all the alertList
        restAlertMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(alert.getId().intValue())))
            .andExpect(jsonPath("$.[*].alertType").value(hasItem(DEFAULT_ALERT_TYPE)))
            .andExpect(jsonPath("$.[*].alertTime").value(hasItem(DEFAULT_ALERT_TIME.toString())))
            .andExpect(jsonPath("$.[*].emailSent").value(hasItem(DEFAULT_EMAIL_SENT)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)));
    }

    @Test
    @Transactional
    void getAlert() throws Exception {
        // Initialize the database
        insertedAlert = alertRepository.saveAndFlush(alert);

        // Get the alert
        restAlertMockMvc
            .perform(get(ENTITY_API_URL_ID, alert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(alert.getId().intValue()))
            .andExpect(jsonPath("$.alertType").value(DEFAULT_ALERT_TYPE))
            .andExpect(jsonPath("$.alertTime").value(DEFAULT_ALERT_TIME.toString()))
            .andExpect(jsonPath("$.emailSent").value(DEFAULT_EMAIL_SENT))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS));
    }

    @Test
    @Transactional
    void getNonExistingAlert() throws Exception {
        // Get the alert
        restAlertMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlert() throws Exception {
        // Initialize the database
        insertedAlert = alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alert
        Alert updatedAlert = alertRepository.findById(alert.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlert are not directly saved in db
        em.detach(updatedAlert);
        updatedAlert.alertType(UPDATED_ALERT_TYPE).alertTime(UPDATED_ALERT_TIME).emailSent(UPDATED_EMAIL_SENT).details(UPDATED_DETAILS);
        AlertDTO alertDTO = alertMapper.toDto(updatedAlert);

        restAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alertDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO))
            )
            .andExpect(status().isOk());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlertToMatchAllProperties(updatedAlert);
    }

    @Test
    @Transactional
    void putNonExistingAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, alertDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(alertDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(alertDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlertWithPatch() throws Exception {
        // Initialize the database
        insertedAlert = alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alert using partial update
        Alert partialUpdatedAlert = new Alert();
        partialUpdatedAlert.setId(alert.getId());

        partialUpdatedAlert.alertType(UPDATED_ALERT_TYPE).alertTime(UPDATED_ALERT_TIME);

        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlert))
            )
            .andExpect(status().isOk());

        // Validate the Alert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlert, alert), getPersistedAlert(alert));
    }

    @Test
    @Transactional
    void fullUpdateAlertWithPatch() throws Exception {
        // Initialize the database
        insertedAlert = alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the alert using partial update
        Alert partialUpdatedAlert = new Alert();
        partialUpdatedAlert.setId(alert.getId());

        partialUpdatedAlert
            .alertType(UPDATED_ALERT_TYPE)
            .alertTime(UPDATED_ALERT_TIME)
            .emailSent(UPDATED_EMAIL_SENT)
            .details(UPDATED_DETAILS);

        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlert))
            )
            .andExpect(status().isOk());

        // Validate the Alert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlertUpdatableFieldsEquals(partialUpdatedAlert, getPersistedAlert(partialUpdatedAlert));
    }

    @Test
    @Transactional
    void patchNonExistingAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, alertDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alertDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(alertDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        alert.setId(longCount.incrementAndGet());

        // Create the Alert
        AlertDTO alertDTO = alertMapper.toDto(alert);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlertMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(alertDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Alert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlert() throws Exception {
        // Initialize the database
        insertedAlert = alertRepository.saveAndFlush(alert);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the alert
        restAlertMockMvc
            .perform(delete(ENTITY_API_URL_ID, alert.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return alertRepository.count();
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

    protected Alert getPersistedAlert(Alert alert) {
        return alertRepository.findById(alert.getId()).orElseThrow();
    }

    protected void assertPersistedAlertToMatchAllProperties(Alert expectedAlert) {
        assertAlertAllPropertiesEquals(expectedAlert, getPersistedAlert(expectedAlert));
    }

    protected void assertPersistedAlertToMatchUpdatableProperties(Alert expectedAlert) {
        assertAlertAllUpdatablePropertiesEquals(expectedAlert, getPersistedAlert(expectedAlert));
    }
}
