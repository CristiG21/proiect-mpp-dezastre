package com.mpp.disaster.domain;

import com.mpp.disaster.domain.enumeration.MessageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A CommunityMessage.
 */
@Entity
@Table(name = "community_message")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @NotNull
    @Column(name = "time_posted", nullable = false)
    private Instant time_posted;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType type;

    @Column(name = "parent_id")
    private Integer parentId;

    @NotNull
    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CommunityMessage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public CommunityMessage content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTime_posted() {
        return this.time_posted;
    }

    public CommunityMessage time_posted(Instant time_posted) {
        this.setTime_posted(time_posted);
        return this;
    }

    public void setTime_posted(Instant time_posted) {
        this.time_posted = time_posted;
    }

    public MessageType getType() {
        return this.type;
    }

    public CommunityMessage type(MessageType type) {
        this.setType(type);
        return this;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer getParentId() {
        return this.parentId;
    }

    public CommunityMessage parentId(Integer parentId) {
        this.setParentId(parentId);
        return this;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Boolean getApproved() {
        return this.approved;
    }

    public CommunityMessage approved(Boolean approved) {
        this.setApproved(approved);
        return this;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CommunityMessage user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunityMessage)) {
            return false;
        }
        return getId() != null && getId().equals(((CommunityMessage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityMessage{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", time_posted='" + getTime_posted() + "'" +
            ", type='" + getType() + "'" +
            ", parentId=" + getParentId() +
            ", approved='" + getApproved() + "'" +
            "}";
    }
}
