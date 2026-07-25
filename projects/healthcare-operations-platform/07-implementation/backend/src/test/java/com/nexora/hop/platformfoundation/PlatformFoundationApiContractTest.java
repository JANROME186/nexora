package com.nexora.hop.platformfoundation;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class PlatformFoundationApiContractTest {

    private static final Path API_CONTRACT = Path.of(
            "..",
            "..",
            "06-delivery",
            "mvp",
            "modules",
            "MVP-MOD-001-platform-foundation",
            "api-contract.openapi.md");

    @Test
    void openApiContractDeclaresImplementedPlatformFoundationEndpoints() throws Exception {
        Map<String, Object> contract = loadContract();
        Map<String, Object> paths = map(contract.get("paths"));

        assertThat(paths.keySet()).contains(
                "/platform/tenants",
                "/platform/tenants/{tenantId}",
                "/organization/laboratories",
                "/organization/laboratories/{laboratoryId}",
                "/organization/branches",
                "/organization/branches/{branchId}",
                "/identity/users",
                "/identity/users/{userId}",
                "/identity/users/{userId}/role-assignments",
                "/audit/events");
    }

    @Test
    void openApiContractDeclaresExpectedOperationsAndSchemas() throws Exception {
        Map<String, Object> contract = loadContract();
        Map<String, Object> paths = map(contract.get("paths"));
        Map<String, Object> schemas = map(map(contract.get("components")).get("schemas"));

        assertOperation(paths, "/platform/tenants", "post", "createTenant");
        assertOperation(paths, "/platform/tenants/{tenantId}", "get", "getTenant");
        assertOperation(paths, "/organization/laboratories", "post", "createLaboratory");
        assertOperation(paths, "/organization/laboratories/{laboratoryId}", "get", "getLaboratory");
        assertOperation(paths, "/organization/branches", "post", "createBranch");
        assertOperation(paths, "/organization/branches/{branchId}", "get", "getBranch");
        assertOperation(paths, "/identity/users", "post", "createUser");
        assertOperation(paths, "/identity/users/{userId}", "get", "getUser");
        assertOperation(paths, "/identity/users/{userId}/role-assignments", "post", "assignRole");
        assertOperation(paths, "/audit/events", "get", "searchAuditEvents");

        assertThat(schemas.keySet()).contains(
                "TenantResponse",
                "LaboratoryResponse",
                "BranchResponse",
                "UserResponse",
                "AssignRoleRequest",
                "AuditEventResponse");
    }

    @Test
    void openApiContractKeepsAuditSearchFilterableByTenantAndSubject() throws Exception {
        Map<String, Object> contract = loadContract();
        Map<String, Object> auditEvents = map(map(contract.get("paths")).get("/audit/events"));
        Map<String, Object> getOperation = map(auditEvents.get("get"));
        List<Map<String, Object>> parameters = list(getOperation.get("parameters"));

        assertThat(parameters)
                .extracting(parameter -> parameter.get("name"))
                .contains("tenantId", "subjectId");
    }

    private static Map<String, Object> loadContract() throws Exception {
        return map(new Yaml().load(extractStructuredPayload(Files.readString(API_CONTRACT))));
    }

    private static String extractStructuredPayload(String markdown) {
        String payloadMarker = "<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->";
        int payloadStart = markdown.indexOf(payloadMarker);
        if (payloadStart >= 0) {
            markdown = markdown.substring(payloadStart);
        }
        String marker = "```yaml\n";
        int start = markdown.indexOf(marker);
        if (start < 0) {
            return markdown;
        }
        start += marker.length();
        int end = markdown.indexOf("\n```", start);
        return end < 0 ? markdown.substring(start) : markdown.substring(start, end);
    }

    private static void assertOperation(
            Map<String, Object> paths,
            String path,
            String method,
            String operationId) {
        Map<String, Object> operations = map(paths.get(path));
        Map<String, Object> operation = map(operations.get(method));

        assertThat(operation.get("operationId")).isEqualTo(operationId);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> list(Object value) {
        return (List<Map<String, Object>>) value;
    }
}
