package com.mpp.disaster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Center.
 */
@Entity
@Table(name = "center")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Center implements Serializable {

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

    @Column(name = "status")
    private Boolean status;

    @Column(name = "description")
    private String description;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "center")
    @JsonIgnoreProperties(value = { "center" }, allowSetters = true)
    private Set<CenterTypeWrapper> types = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "center")
    @JsonIgnoreProperties(value = { "center" }, allowSetters = true)
    private Set<PhotoURL> photoUrls = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Center id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Center name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Center longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Center latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Center status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public Center description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAvailableSeats() {
        return this.availableSeats;
    }

    public Center availableSeats(Integer availableSeats) {
        this.setAvailableSeats(availableSeats);
        return this;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Set<CenterTypeWrapper> getTypes() {
        return this.types;
    }

    public void setTypes(Set<CenterTypeWrapper> centerTypeWrappers) {
        if (this.types != null) {
            this.types.forEach(i -> i.setCenter(null));
        }
        if (centerTypeWrappers != null) {
            centerTypeWrappers.forEach(i -> i.setCenter(this));
        }
        this.types = centerTypeWrappers;
    }

    public Center types(Set<CenterTypeWrapper> centerTypeWrappers) {
        this.setTypes(centerTypeWrappers);
        return this;
    }

    public Center addTypes(CenterTypeWrapper centerTypeWrapper) {
        this.types.add(centerTypeWrapper);
        centerTypeWrapper.setCenter(this);
        return this;
    }

    public Center removeTypes(CenterTypeWrapper centerTypeWrapper) {
        this.types.remove(centerTypeWrapper);
        centerTypeWrapper.setCenter(null);
        return this;
    }

    public Set<PhotoURL> getPhotoUrls() {
        return this.photoUrls;
    }

    public void setPhotoUrls(Set<PhotoURL> photoURLS) {
        if (this.photoUrls != null) {
            this.photoUrls.forEach(i -> i.setCenter(null));
        }
        if (photoURLS != null) {
            photoURLS.forEach(i -> i.setCenter(this));
        }
        this.photoUrls = photoURLS;
    }

    public Center photoUrls(Set<PhotoURL> photoURLS) {
        this.setPhotoUrls(photoURLS);
        return this;
    }

    public Center addPhotoUrl(PhotoURL photoURL) {
        this.photoUrls.add(photoURL);
        photoURL.setCenter(this);
        return this;
    }

    public Center removePhotoUrl(PhotoURL photoURL) {
        this.photoUrls.remove(photoURL);
        photoURL.setCenter(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Center)) {
            return false;
        }
        return getId() != null && getId().equals(((Center) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Center{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", availableSeats=" + getAvailableSeats() +
            "}";
    }
}
