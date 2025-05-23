package com.mpp.disaster.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mpp.disaster.domain.OfficialMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfficialMessageDTO implements Serializable {

    private Long id;

    private String title;

    @NotNull
    private String body;

    @NotNull
    private Instant timePosted;

    @NotNull
    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(Instant timePosted) {
        this.timePosted = timePosted;
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
        if (!(o instanceof OfficialMessageDTO)) {
            return false;
        }

        OfficialMessageDTO officialMessageDTO = (OfficialMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, officialMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfficialMessageDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", body='" + getBody() + "'" +
            ", timePosted='" + getTimePosted() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
