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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads.CatalogPublicReadPort.PublicPreparationSnapshot;
import com.nexora.hop.platformfoundation.publicweb.PublicWebConstants;
import com.nexora.hop.platformfoundation.publicweb.PublicWebErrorCodes;
import com.nexora.hop.platformfoundation.publicweb.PublicWebException;

/**
 * Anonymous public read of published preparation instructions (BCM-SVC-005 public_surface).
 * Only fully-localized, published records are returned.
 */
@RestController
@RequestMapping(PublicWebConstants.CATALOG_BASE_PATH + "/preparations")
class PublicPreparationController {

    private final CatalogPublicReadPort readPort;

    PublicPreparationController(CatalogPublicReadPort readPort) {
        this.readPort = readPort;
    }

    @GetMapping("/published")
    ResponseEntity<List<PublicPreparationSnapshot>> listPublished(@RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(readPort.listPublishedPreparations(laboratoryId));
    }

    @GetMapping("/{preparationId}/published-snapshot")
    ResponseEntity<PublicPreparationSnapshot> getPublishedSnapshot(@PathVariable String preparationId) {
        return ResponseEntity.ok(readPort.findPublishedPreparationSnapshot(preparationId)
                .orElseThrow(() -> new PublicWebException(HttpStatus.NOT_FOUND,
                        PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED,
                        "Requested preparation is not published.")));
    }
}
