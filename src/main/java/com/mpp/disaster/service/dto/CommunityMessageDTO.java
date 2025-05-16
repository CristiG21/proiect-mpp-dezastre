package com.mpp.disaster.service.dto;

import com.mpp.disaster.domain.enumeration.MessageType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
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
    private MessageType type;

    private Integer parentId;

    @NotNull
    private Boolean approved;

    @NotNull
    private UserDTO user;

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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
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
            ", type='" + getType() + "'" +
            ", parentId=" + getParentId() +
            ", approved='" + getApproved() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
