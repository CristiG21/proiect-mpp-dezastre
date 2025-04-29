package com.mpp.disaster.domain;

import com.mpp.disaster.domain.enumeration.DisasterType;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Disaster.
 */
@Entity
@Table(name = "disaster")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Disaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "radius")
    private Double radius;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DisasterType type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Disaster id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Disaster name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Disaster longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Disaster latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getRadius() {
        return this.radius;
    }

    public Disaster radius(Double radius) {
        this.setRadius(radius);
        return this;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public DisasterType getType() {
        return this.type;
    }

    public Disaster type(DisasterType type) {
        this.setType(type);
        return this;
    }

    public void setType(DisasterType type) {
        this.type = type;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Disaster)) {
            return false;
        }
        return getId() != null && getId().equals(((Disaster) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Disaster{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", radius=" + getRadius() +
            ", type='" + getType() + "'" +
            "}";
    }
}
