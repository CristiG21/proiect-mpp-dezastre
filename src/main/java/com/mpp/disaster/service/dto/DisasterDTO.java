package com.mpp.disaster.service.dto;

import com.mpp.disaster.domain.enumeration.DisasterType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mpp.disaster.domain.Disaster} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisasterDTO implements Serializable {

    private Long id;

    private String name;

    private Double longitude;

    private Double latitude;

    private Double radius;

    private DisasterType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public DisasterType getType() {
        return type;
    }

    public void setType(DisasterType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisasterDTO)) {
            return false;
        }

        DisasterDTO disasterDTO = (DisasterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, disasterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisasterDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", radius=" + getRadius() +
            ", type='" + getType() + "'" +
            "}";
    }
}
