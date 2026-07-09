package com.nexora.hop.platformfoundation.catalogtestconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.Yaml;

/**
 * Confirms every generatable operation declared in each Diagnostic Catalog capability's
 * openapi-source.yaml (the source contract model per the capability-package-standard) is
 * reachable as a registered Spring MVC route in the compiled catalog-test-configuration module.
 * This is the MVP-MOD-002-BE-001 equivalent of PlatformFoundationApiContractTest, pointed at the
 * capability packages instead of a rendered module OpenAPI document (no MVP-MOD-002 module
 * folder exists yet under 06-delivery/mvp/modules; capability packages remain the source of truth).
 */
@SpringBootTest
class CatalogTestConfigurationContractTest {

    private static final List<String> CAPABILITY_PACKAGES = List.of(
            "bcm-svc-001-diagnostic-service-catalog",
            "bcm-svc-002-test-catalog",
            "bcm-svc-003-panel-catalog",
            "bcm-svc-004-analyte-catalog",
            "bcm-svc-005-patient-preparation-management",
            "bcm-svc-006-reference-range-management",
            "bcm-svc-007-sample-catalog",
            "bcm-svc-009-price-list-management");

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void everyCapabilityOperationIsRegisteredAsASpringRoute() throws Exception {
        Set<String> registeredRoutes = registeredRoutes();

        List<String> missing = new ArrayList<>();
        for (String packageName : CAPABILITY_PACKAGES) {
            Map<String, Object> contract = loadOpenApiSource(packageName);
            Map<String, Object> api = map(contract.get("api"));
            String basePath = (String) api.get("base_path");
            List<Map<String, Object>> resources = list(contract.get("resources"));

            for (Map<String, Object> resource : resources) {
                List<Map<String, Object>> operations = list(resource.get("operations"));
                for (Map<String, Object> operation : operations) {
                    String method = ((String) operation.get("method")).toUpperCase();
                    String path = (String) operation.get("path");
                    String fullPath = join(basePath, path);
                    String route = method + " " + fullPath;
                    if (!registeredRoutes.contains(route)) {
                        missing.add(packageName + " -> " + operation.get("id") + " (" + route + ")");
                    }
                }
            }
        }

        assertThat(missing).as("Operations declared in openapi-source.yaml but not registered as Spring routes").isEmpty();
    }

    private Set<String> registeredRoutes() {
        Set<String> routes = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            Set<String> patterns = info.getPatternValues();
            for (RequestMethod method : methods) {
                for (String pattern : patterns) {
                    routes.add(method.name() + " " + pattern);
                }
            }
        }
        return routes;
    }

    private static String join(String basePath, String path) {
        String base = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String suffix = "/".equals(path) ? "" : (path.startsWith("/") ? path : "/" + path);
        return base + suffix;
    }

    private static Map<String, Object> loadOpenApiSource(String packageName) throws Exception {
        Path source = Path.of(
                "..", "..", "01-product-definition", "business-capabilities", "packages", packageName,
                "openapi-source.yaml");
        try (InputStream inputStream = Files.newInputStream(source)) {
            return map(new Yaml().load(inputStream));
        }
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
