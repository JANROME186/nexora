/**
 * Catalog Test Configuration published-only read port (COM-MOD-011-BE-001). Named interface
 * exposing an anonymous, published-only projection over BCM-SVC-001/002/003/005 aggregates so
 * the publicweb module never depends on internal catalog application classes.
 */
@org.springframework.modulith.NamedInterface("catalog-public-read-port")
package com.nexora.hop.platformfoundation.catalogtestconfiguration.publicreads;
