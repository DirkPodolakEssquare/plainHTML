package de.essquare.driftbottle;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class ResourceServerConfiguration {

}

//import java.io.IOException;
//import static java.nio.charset.StandardCharsets.UTF_8;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
//import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
//import org.springframework.web.client.DefaultResponseErrorHandler;
//import org.springframework.web.client.ResponseErrorHandler;
//import org.springframework.web.client.RestTemplate;
//import static org.springframework.util.StreamUtils.copyToString;
//
//import de.sellwerk.sde.auth.AuthService;
//import de.sellwerk.sde.auth.AuthServiceRequestInterceptor;
//import de.sellwerk.sde.auth.AuthUserAuthenticationConverter;
//import de.sellwerk.sde.auth.AuthenticationOAuthTranslator;
//import de.sellwerk.sde.auth.SdeOAuth2AuthenticationManager;
//
//@EnableResourceServer
//@Configuration
//public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
//
//    private static final Logger LOG = LoggerFactory.getLogger(ResourceServerConfiguration.class);
//
//    @Bean
//    public AuthService authService() {
//        return new AuthService();
//    }
//
//    @Bean
//    public RestTemplate companyRestTemplate() {
//        final RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getInterceptors().add(new AuthServiceRequestInterceptor(authService()));
//        restTemplate.setErrorHandler(responseErrorHandler());
//        return restTemplate;
//    }
//
//    private ResponseErrorHandler responseErrorHandler() {
//        return new DefaultResponseErrorHandler() {
//            @Override
//            protected void handleError(final ClientHttpResponse response, final HttpStatus statusCode) throws IOException {
//                final String content = copyToString(response.getBody(), UTF_8);
//                LOG.error("Server returned statusCode: {} with unexpected content: {}", statusCode, content);
//                super.handleError(response, statusCode);
//            }
//        };
//    }
//
//    @Bean
//    public TokenStore jwkTokenStore(final ResourceServerProperties resource) {
//        final DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
//        tokenConverter.setUserTokenConverter(new AuthUserAuthenticationConverter());
//        return new JwkTokenStore(resource.getJwk().getKeySetUri(), tokenConverter);
//    }
//
//    @Override
//    public void configure(final ResourceServerSecurityConfigurer resources) {
//        // Default authentication entry point from resources with our own translator
//        final OAuth2AuthenticationEntryPoint authEntryPoint = new OAuth2AuthenticationEntryPoint();
//        authEntryPoint.setExceptionTranslator(new AuthenticationOAuthTranslator());
//
//        // Custom authentication manager to avoid 500 code on invalid authentication token
//        final SdeOAuth2AuthenticationManager authManager = new SdeOAuth2AuthenticationManager();
//
//        resources.resourceId(null)
//                 .authenticationEntryPoint(authEntryPoint)
//                 .authenticationManager(authManager);
//    }
//
//    @Override
//    public void configure(final HttpSecurity http) throws Exception {
//        http
//                .cors()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/actuator/health")
//                .permitAll()
//                .anyRequest()
//                .authenticated();
//    }
//}
