package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Log} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LogDTO implements Serializable {

    private Long id;

    @NotNull
    private String eventType;

    private Instant eventTime;

    private String ipAddress;

    private String description;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof LogDTO)) {
            return false;
        }

        LogDTO logDTO = (LogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, logDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LogDTO{" +
            "id=" + getId() +
            ", eventType='" + getEventType() + "'" +
            ", eventTime='" + getEventTime() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", description='" + getDescription() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
