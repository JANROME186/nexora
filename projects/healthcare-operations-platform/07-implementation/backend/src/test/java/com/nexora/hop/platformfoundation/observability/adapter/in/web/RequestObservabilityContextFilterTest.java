package com.nexora.hop.platformfoundation.observability.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

class RequestObservabilityContextFilterTest {

    private final RequestObservabilityContextFilter filter = new RequestObservabilityContextFilter();

    @Test
    void populatesMdcFromHeadersAndClearsItAfterTheChainCompletes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestObservabilityContextFilter.TENANT_ID_HEADER, "tenant-42");
        request.addHeader(RequestObservabilityContextFilter.USER_ID_HEADER, "user-7");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);
        StringBuilder observedTenantId = new StringBuilder();
        StringBuilder observedUserId = new StringBuilder();
        StringBuilder observedTraceId = new StringBuilder();
        org.mockito.Mockito.doAnswer(invocation -> {
            observedTenantId.append(MDC.get(RequestObservabilityContextFilter.MDC_TENANT_ID));
            observedUserId.append(MDC.get(RequestObservabilityContextFilter.MDC_USER_ID));
            observedTraceId.append(MDC.get(RequestObservabilityContextFilter.MDC_TRACE_ID));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        assertThat(observedTenantId.toString()).isEqualTo("tenant-42");
        assertThat(observedUserId.toString()).isEqualTo("user-7");
        assertThat(observedTraceId.toString()).isNotBlank();
        assertThat(response.getHeader(RequestObservabilityContextFilter.TRACE_ID_RESPONSE_HEADER)).isNotBlank();
        assertThat(MDC.get(RequestObservabilityContextFilter.MDC_TENANT_ID)).isNull();
        assertThat(MDC.get(RequestObservabilityContextFilter.MDC_USER_ID)).isNull();
        assertThat(MDC.get(RequestObservabilityContextFilter.MDC_TRACE_ID)).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void defaultsMissingTenantAndUserHeadersToUnknown() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        StringBuilder observedTenantId = new StringBuilder();
        org.mockito.Mockito.doAnswer(invocation -> {
            observedTenantId.append(MDC.get(RequestObservabilityContextFilter.MDC_TENANT_ID));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        assertThat(observedTenantId.toString()).isEqualTo("unknown");
    }

    @Test
    void reusesTraceIdSegmentFromInboundTraceparentHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                RequestObservabilityContextFilter.TRACE_PARENT_HEADER,
                "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getHeader(RequestObservabilityContextFilter.TRACE_ID_RESPONSE_HEADER))
                .isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
        verify(chain).doFilter(request, response);
    }

    @Test
    void rejectsMalformedTraceparentAndMintsANewTraceIdInstead() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestObservabilityContextFilter.TRACE_PARENT_HEADER, "not-a-real-traceparent");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        String traceId = response.getHeader(RequestObservabilityContextFilter.TRACE_ID_RESPONSE_HEADER);
        assertThat(traceId).isNotBlank().doesNotContain("not-a-real-traceparent").matches("^[0-9a-f]{32}$");
    }

    @Test
    void stripsControlCharactersFromTenantAndUserHeadersBeforeLogging() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestObservabilityContextFilter.TENANT_ID_HEADER, "tenant-1\r\nFORGED LOG LINE");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        StringBuilder observedTenantId = new StringBuilder();
        org.mockito.Mockito.doAnswer(invocation -> {
            observedTenantId.append(MDC.get(RequestObservabilityContextFilter.MDC_TENANT_ID));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        assertThat(observedTenantId.toString()).isEqualTo("tenant-1FORGED LOG LINE").doesNotContain("\r", "\n");
    }
}
