package com.nexora.hop.platformfoundation.aioverlay;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * BCM-AI-006 qa_focus requires an explicit vendor_lock_in_scan alongside safety, explainability
 * and human_control evidence. AiDraftGeneratorPort (a replaceable functional interface) is the
 * only integration seam the AI overlay uses; these checks assert no token-billed proprietary
 * model-provider SDK dependency has been introduced through the build or the compiled adapters.
 */
class AiOverlayVendorNeutralityTest {

    private static final Path BACKEND_POM = Path.of("pom.xml");

    private static final List<String> FORBIDDEN_VENDOR_SDK_MARKERS = List.of(
            "com.openai",
            "com.azure:azure-ai",
            "com.google.cloud:google-cloud-aiplatform",
            "com.amazonaws:aws-java-sdk-bedrock",
            "software.amazon.awssdk:bedrock",
            "com.anthropic",
            "ai.cohere",
            "com.theokanning.openai-gpt3-java");

    @Test
    void buildDeclaresNoProprietaryModelProviderSdkDependency() throws IOException {
        String pom = Files.readString(BACKEND_POM).toLowerCase(Locale.ROOT);

        assertThat(FORBIDDEN_VENDOR_SDK_MARKERS)
                .as("backend pom.xml must not depend on a token-billed proprietary AI SDK")
                .allSatisfy(marker -> assertThat(pom).doesNotContain(marker.toLowerCase(Locale.ROOT)));
    }

    @Test
    void assistantServiceOnlyDependsOnTheReplaceableDraftGeneratorPort() throws IOException {
        Path servicePath = Path.of(
                "src", "main", "java", "com", "nexora", "hop", "platformfoundation", "aioverlay",
                "assistant", "application", "AiAssistantService.java");
        String source = Files.readString(servicePath);

        assertThat(source).contains("AiDraftGeneratorPort");
        assertThat(FORBIDDEN_VENDOR_SDK_MARKERS)
                .as("AiAssistantService must integrate only through AiDraftGeneratorPort, never a vendor SDK type")
                .allSatisfy(marker -> assertThat(source.toLowerCase(Locale.ROOT))
                        .doesNotContain(marker.toLowerCase(Locale.ROOT)));
    }
}
