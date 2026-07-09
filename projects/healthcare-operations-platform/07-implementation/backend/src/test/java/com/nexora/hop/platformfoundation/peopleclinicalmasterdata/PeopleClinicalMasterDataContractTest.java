package com.nexora.hop.platformfoundation.peopleclinicalmasterdata;

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
 * Confirms every operation declared in the People and Clinical Master Data openapi-source models
 * resolves to a registered Spring MVC route in the compiled peopleclinicalmasterdata module.
 * Mirrors {@code CatalogTestConfigurationContractTest}.
 */
@SpringBootTest
class PeopleClinicalMasterDataContractTest {

    private static final List<String> CAPABILITY_PACKAGES = List.of(
            "bcm-per-001-person-management",
            "bcm-per-002-patient-management",
            "bcm-per-003-doctor-management",
            "bcm-att-002-patient-registration");

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

        assertThat(missing)
                .as("Operations declared in openapi-source.yaml but not registered as Spring routes")
                .isEmpty();
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
        Path source = Path.of("..", "..", "01-product-definition", "business-capabilities", "packages",
                packageName, "openapi-source.yaml");
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
