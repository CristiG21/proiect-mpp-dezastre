package com.mpp.disaster.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mpp.disaster.domain.PhotoURL} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PhotoURLDTO implements Serializable {

    private Long id;

    private String url;

    private CenterDTO center;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        if (!(o instanceof PhotoURLDTO)) {
            return false;
        }

        PhotoURLDTO photoURLDTO = (PhotoURLDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, photoURLDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PhotoURLDTO{" +
            "id=" + getId() +
            ", url='" + getUrl() + "'" +
            ", center=" + getCenter() +
            "}";
    }
}
