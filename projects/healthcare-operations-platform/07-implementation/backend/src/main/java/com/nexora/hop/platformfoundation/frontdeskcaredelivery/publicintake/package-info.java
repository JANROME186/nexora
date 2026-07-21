/**
 * FrontDeskCareDelivery public-intake port (COM-MOD-011-BE-001). Named interface exposing a
 * stable adapter boundary for anonymous public-website appointment (RN-008) and quotation
 * (RN-009) intake, so the publicweb module never depends on internal appointment or quotation
 * application classes.
 */
@org.springframework.modulith.NamedInterface("public-intake-port")
package com.nexora.hop.platformfoundation.frontdeskcaredelivery.publicintake;
