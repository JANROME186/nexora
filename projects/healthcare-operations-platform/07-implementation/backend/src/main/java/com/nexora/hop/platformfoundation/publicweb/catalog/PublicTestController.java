package com.nexora.hop.platformfoundation.publicweb.catalog;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads.CatalogPublicReadPort;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads.CatalogPublicReadPort.PublicTestSnapshot;
import com.nexora.hop.platformfoundation.publicweb.PublicWebConstants;
import com.nexora.hop.platformfoundation.publicweb.PublicWebErrorCodes;
import com.nexora.hop.platformfoundation.publicweb.PublicWebException;

/**
 * Anonymous public read of published diagnostic tests (BCM-SVC-002 public_surface, RN-004).
 */
@RestController
@RequestMapping(PublicWebConstants.CATALOG_BASE_PATH + "/tests")
class PublicTestController {

    private final CatalogPublicReadPort readPort;

    PublicTestController(CatalogPublicReadPort readPort) {
        this.readPort = readPort;
    }

    @GetMapping("/published")
    ResponseEntity<List<PublicTestSnapshot>> listPublished(@RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(readPort.listPublishedTests(laboratoryId));
    }

    @GetMapping("/{testId}/published-snapshot")
    ResponseEntity<PublicTestSnapshot> getPublishedSnapshot(@PathVariable String testId) {
        return ResponseEntity.ok(readPort.findPublishedTestSnapshot(testId)
                .orElseThrow(() -> new PublicWebException(HttpStatus.NOT_FOUND,
                        PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED,
                        "Requested test definition is not published.")));
    }
}
