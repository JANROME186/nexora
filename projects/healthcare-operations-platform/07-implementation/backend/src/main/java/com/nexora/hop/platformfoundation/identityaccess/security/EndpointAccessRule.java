package com.nexora.hop.platformfoundation.identityaccess.security;

import com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode;

/** Permission and action required by an API endpoint. */
public record EndpointAccessRule(
    PermissionCode permission, AccessAction action, String capability) {}
