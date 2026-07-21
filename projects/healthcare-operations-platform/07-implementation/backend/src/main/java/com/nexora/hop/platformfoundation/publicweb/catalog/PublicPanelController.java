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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads.CatalogPublicReadPort.PublicPanelSnapshot;
import com.nexora.hop.platformfoundation.publicweb.PublicWebConstants;
import com.nexora.hop.platformfoundation.publicweb.PublicWebErrorCodes;
import com.nexora.hop.platformfoundation.publicweb.PublicWebException;

/**
 * Anonymous public read of published diagnostic panels (BCM-SVC-003 public_surface, RN-004).
 */
@RestController
@RequestMapping(PublicWebConstants.CATALOG_BASE_PATH + "/panels")
class PublicPanelController {

    private final CatalogPublicReadPort readPort;

    PublicPanelController(CatalogPublicReadPort readPort) {
        this.readPort = readPort;
    }

    @GetMapping("/published")
    ResponseEntity<List<PublicPanelSnapshot>> listPublished(@RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(readPort.listPublishedPanels(laboratoryId));
    }

    @GetMapping("/{panelId}/published-snapshot")
    ResponseEntity<PublicPanelSnapshot> getPublishedSnapshot(@PathVariable String panelId) {
        return ResponseEntity.ok(readPort.findPublishedPanelSnapshot(panelId)
                .orElseThrow(() -> new PublicWebException(HttpStatus.NOT_FOUND,
                        PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED,
                        "Requested panel is not published.")));
    }
}
