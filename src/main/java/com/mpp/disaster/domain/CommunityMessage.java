package com.mpp.disaster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "parent_id")
    private Integer parentId;

    @NotNull
    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "parent", "replies" }, allowSetters = true)
    private CommunityMessage parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnoreProperties(value = { "user", "parent", "replies" }, allowSetters = true)
    private Set<CommunityMessage> replies = new HashSet<>();

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

    public CommunityMessage getParent() {
        return this.parent;
    }

    public void setParent(CommunityMessage communityMessage) {
        this.parent = communityMessage;
    }

    public CommunityMessage parent(CommunityMessage communityMessage) {
        this.setParent(communityMessage);
        return this;
    }

    public Set<CommunityMessage> getReplies() {
        return this.replies;
    }

    public void setReplies(Set<CommunityMessage> communityMessages) {
        if (this.replies != null) {
            this.replies.forEach(i -> i.setParent(null));
        }
        if (communityMessages != null) {
            communityMessages.forEach(i -> i.setParent(this));
        }
        this.replies = communityMessages;
    }

    public CommunityMessage replies(Set<CommunityMessage> communityMessages) {
        this.setReplies(communityMessages);
        return this;
    }

    public CommunityMessage addReplies(CommunityMessage communityMessage) {
        this.replies.add(communityMessage);
        communityMessage.setParent(this);
        return this;
    }

    public CommunityMessage removeReplies(CommunityMessage communityMessage) {
        this.replies.remove(communityMessage);
        communityMessage.setParent(null);
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
            ", parentId=" + getParentId() +
            ", approved='" + getApproved() + "'" +
            "}";
    }
}
