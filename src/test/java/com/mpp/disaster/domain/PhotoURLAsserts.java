package com.mpp.disaster.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PhotoURLAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPhotoURLAllPropertiesEquals(PhotoURL expected, PhotoURL actual) {
        assertPhotoURLAutoGeneratedPropertiesEquals(expected, actual);
        assertPhotoURLAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPhotoURLAllUpdatablePropertiesEquals(PhotoURL expected, PhotoURL actual) {
        assertPhotoURLUpdatableFieldsEquals(expected, actual);
        assertPhotoURLUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPhotoURLAutoGeneratedPropertiesEquals(PhotoURL expected, PhotoURL actual) {
        assertThat(actual)
            .as("Verify PhotoURL auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPhotoURLUpdatableFieldsEquals(PhotoURL expected, PhotoURL actual) {
        assertThat(actual)
            .as("Verify PhotoURL relevant properties")
            .satisfies(a -> assertThat(a.getUrl()).as("check url").isEqualTo(expected.getUrl()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPhotoURLUpdatableRelationshipsEquals(PhotoURL expected, PhotoURL actual) {
        assertThat(actual)
            .as("Verify PhotoURL relationships")
            .satisfies(a -> assertThat(a.getCenter()).as("check center").isEqualTo(expected.getCenter()));
    }
}
