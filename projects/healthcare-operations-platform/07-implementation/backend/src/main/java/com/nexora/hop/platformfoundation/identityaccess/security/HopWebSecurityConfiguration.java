package com.nexora.hop.platformfoundation.identityaccess.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security integration point; request authorization is enforced by {@link
 * HopAuthorizationInterceptor}.
 */
@Configuration
@EnableConfigurationProperties(HopSecurityProperties.class)
public class HopWebSecurityConfiguration implements WebMvcConfigurer {

  private final HopAuthorizationInterceptor authorizationInterceptor;

  public HopWebSecurityConfiguration(HopAuthorizationInterceptor authorizationInterceptor) {
    this.authorizationInterceptor = authorizationInterceptor;
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
        .build();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authorizationInterceptor).addPathPatterns("/api/**");
  }
}
