package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.LogAsserts.*;
import static com.mycompany.myapp.domain.LogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogMapperTest {

    private LogMapper logMapper;

    @BeforeEach
    void setUp() {
        logMapper = new LogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLogSample1();
        var actual = logMapper.toEntity(logMapper.toDto(expected));
        assertLogAllPropertiesEquals(expected, actual);
    }
}
