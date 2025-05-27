package com.mpp.disaster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mpp.disaster.domain.enumeration.CenterType;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A CenterTypeWrapper.
 */
@Entity
@Table(name = "center_type_wrapper")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CenterTypeWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CenterType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "types", "user", "photoUrls" }, allowSetters = true)
    private Center center;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CenterTypeWrapper id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CenterType getType() {
        return this.type;
    }

    public CenterTypeWrapper type(CenterType type) {
        this.setType(type);
        return this;
    }

    public void setType(CenterType type) {
        this.type = type;
    }

    public Center getCenter() {
        return this.center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public CenterTypeWrapper center(Center center) {
        this.setCenter(center);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CenterTypeWrapper)) {
            return false;
        }
        return getId() != null && getId().equals(((CenterTypeWrapper) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CenterTypeWrapper{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
