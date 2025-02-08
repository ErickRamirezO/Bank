package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Alert.
 */
@Entity
@Table(name = "alert")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Column(name = "alert_time")
    private Instant alertTime;

    @NotNull
    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent;

    @Column(name = "details")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Alert id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertType() {
        return this.alertType;
    }

    public Alert alertType(String alertType) {
        this.setAlertType(alertType);
        return this;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Instant getAlertTime() {
        return this.alertTime;
    }

    public Alert alertTime(Instant alertTime) {
        this.setAlertTime(alertTime);
        return this;
    }

    public void setAlertTime(Instant alertTime) {
        this.alertTime = alertTime;
    }

    public Boolean getEmailSent() {
        return this.emailSent;
    }

    public Alert emailSent(Boolean emailSent) {
        this.setEmailSent(emailSent);
        return this;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getDetails() {
        return this.details;
    }

    public Alert details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Alert user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alert)) {
            return false;
        }
        return getId() != null && getId().equals(((Alert) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Alert{" +
            "id=" + getId() +
            ", alertType='" + getAlertType() + "'" +
            ", alertTime='" + getAlertTime() + "'" +
            ", emailSent='" + getEmailSent() + "'" +
            ", details='" + getDetails() + "'" +
            "}";
    }
}
