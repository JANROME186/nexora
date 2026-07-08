package com.nexora.hop.platformfoundation;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class PlatformFoundationModulithTest {

    @Test
    void moduleBoundariesAreValid() {
        ApplicationModules.of(PlatformFoundationApplication.class).verify();
    }
}
