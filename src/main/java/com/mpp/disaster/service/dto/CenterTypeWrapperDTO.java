package com.mpp.disaster.service.dto;

import com.mpp.disaster.domain.enumeration.CenterType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mpp.disaster.domain.CenterTypeWrapper} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CenterTypeWrapperDTO implements Serializable {

    private Long id;

    private CenterType type;

    private CenterDTO center;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CenterType getType() {
        return type;
    }

    public void setType(CenterType type) {
        this.type = type;
    }

    public CenterDTO getCenter() {
        return center;
    }

    public void setCenter(CenterDTO center) {
        this.center = center;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CenterTypeWrapperDTO)) {
            return false;
        }

        CenterTypeWrapperDTO centerTypeWrapperDTO = (CenterTypeWrapperDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, centerTypeWrapperDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CenterTypeWrapperDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", center=" + getCenter() +
            "}";
    }
}
