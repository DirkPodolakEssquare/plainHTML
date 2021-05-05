package de.essquare.driftbottle.configuration;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.annotation.Configurations;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EnableWebSecurity
@EnableWebMvc
@Configuration
public class WebConfiguration
        extends WebSecurityConfigurerAdapter
        implements WebMvcConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(Configurations.class);

    private final String[] allowedOrigins;

    public WebConfiguration(@Value("${web.allowed.origins}") String[] allowedOrigins) {
        if (allowedOrigins.length == 0) {
            throw new IllegalArgumentException("'web.allowed.origins' may not be empty");
        }

        if (Arrays.stream(allowedOrigins).anyMatch(StringUtils::isEmpty)) {
            throw new IllegalArgumentException("'web.allowed.origins' may not contain any empty values");
        }

        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(allowedOrigins);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                  .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                  .forEach(jacksonConv -> reconfigureJacksonConverter((MappingJackson2HttpMessageConverter) jacksonConv));
    }

    private void reconfigureJacksonConverter(MappingJackson2HttpMessageConverter jacksonConverter) {
        ObjectMapper objectMapper = jacksonConverter.getObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/public/**", "/actuator/health")
                .permitAll()
                .anyRequest()
                .authenticated();
    }
}
