package com.nexora.hop.platformfoundation.imagingoperations;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ImagingOperationsModuleTest {

    @Test
    void verifiesModuleStructure() {
        ApplicationModules modules = ApplicationModules.of(com.nexora.hop.platformfoundation.PlatformFoundationApplication.class);
        modules.verify();
    }
}
