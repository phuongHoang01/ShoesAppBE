package com.shoesapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SizeMapperTest {

    private SizeMapper sizeMapper;

    @BeforeEach
    public void setUp() {
        sizeMapper = new SizeMapperImpl();
    }
}
