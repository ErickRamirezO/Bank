package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.LogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Log.class);
        Log log1 = getLogSample1();
        Log log2 = new Log();
        assertThat(log1).isNotEqualTo(log2);

        log2.setId(log1.getId());
        assertThat(log1).isEqualTo(log2);

        log2 = getLogSample2();
        assertThat(log1).isNotEqualTo(log2);
    }
}
