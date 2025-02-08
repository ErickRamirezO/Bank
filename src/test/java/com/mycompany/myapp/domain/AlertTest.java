package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AlertTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlertTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Alert.class);
        Alert alert1 = getAlertSample1();
        Alert alert2 = new Alert();
        assertThat(alert1).isNotEqualTo(alert2);

        alert2.setId(alert1.getId());
        assertThat(alert1).isEqualTo(alert2);

        alert2 = getAlertSample2();
        assertThat(alert1).isNotEqualTo(alert2);
    }
}
