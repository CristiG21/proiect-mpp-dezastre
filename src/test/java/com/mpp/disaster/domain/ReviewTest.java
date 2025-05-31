package com.mpp.disaster.domain;

import static com.mpp.disaster.domain.CenterTestSamples.*;
import static com.mpp.disaster.domain.ReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mpp.disaster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void centerTest() {
        Review review = getReviewRandomSampleGenerator();
        Center centerBack = getCenterRandomSampleGenerator();

        review.setCenter(centerBack);
        assertThat(review.getCenter()).isEqualTo(centerBack);

        review.center(null);
        assertThat(review.getCenter()).isNull();
    }
}
