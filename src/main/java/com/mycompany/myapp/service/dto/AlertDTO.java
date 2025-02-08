package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Alert} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlertDTO implements Serializable {

    private Long id;

    @NotNull
    private String alertType;

    private Instant alertTime;

    @NotNull
    private Boolean emailSent;

    private String details;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Instant getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Instant alertTime) {
        this.alertTime = alertTime;
    }

    public Boolean getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlertDTO)) {
            return false;
        }

        AlertDTO alertDTO = (AlertDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, alertDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlertDTO{" +
            "id=" + getId() +
            ", alertType='" + getAlertType() + "'" +
            ", alertTime='" + getAlertTime() + "'" +
            ", emailSent='" + getEmailSent() + "'" +
            ", details='" + getDetails() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
