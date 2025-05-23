package com.mpp.disaster.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.mpp.disaster.domain.CommunityMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityMessageDTO implements Serializable {

    private Long id;

    @NotNull
    private String content;

    @NotNull
    private Instant time_posted;

    @NotNull
    private Boolean approved;

    private ZonedDateTime timeApproved;

    @NotNull
    private UserDTO user;

    private CommunityMessageDTO parent;

    private List<CommunityMessageDTO> replies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTime_posted() {
        return time_posted;
    }

    public void setTime_posted(Instant time_posted) {
        this.time_posted = time_posted;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public ZonedDateTime getTimeApproved() {
        return timeApproved;
    }

    public void setTimeApproved(ZonedDateTime timeApproved) {
        this.timeApproved = timeApproved;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CommunityMessageDTO getParent() {
        return parent;
    }

    public void setParent(CommunityMessageDTO parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunityMessageDTO)) {
            return false;
        }

        CommunityMessageDTO communityMessageDTO = (CommunityMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, communityMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityMessageDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", time_posted='" + getTime_posted() + "'" +
            ", approved='" + getApproved() + "'" +
            ", timeApproved='" + getTimeApproved() + "'" +
            ", user=" + getUser() +
            ", parent=" + getParent() +
            "}";
    }

    public List<CommunityMessageDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommunityMessageDTO> replies) {
        this.replies = replies;
    }
}
