package br.com.fiap.valky.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security stateless com JWT (HS256).
 *
 * Fluxo: POST /api/auth/login devolve um token; o app manda
 * "Authorization: Bearer <token>" e o resource server valida assinatura e
 * validade. O papel vem da claim "roles" e vira ROLE_CIDADA / ROLE_GERENTE.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int TAMANHO_MINIMO_SEGREDO_BYTES = 32;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${valky.jwt.secret}") String segredo) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave(segredo)));
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${valky.jwt.secret}") String segredo) {
        return NimbusJwtDecoder.withSecretKey(chave(segredo))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao ->
                        sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/error").permitAll()
                        // somente GERENTE
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("GERENTE")
                        .requestMatchers("/api/gestao/**").hasRole("GERENTE")
                        // o restante exige token válido (regras finas ficam no AcessoService)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint((request, response, ex) ->
                                escreverErro(response, 401, "Não autorizado",
                                        "Token ausente, inválido ou expirado."))
                        .accessDeniedHandler((request, response, ex) ->
                                escreverErro(response, 403, "Acesso negado",
                                        "Seu perfil não tem permissão para este recurso.")));

        return http.build();
    }

    private static void escreverErro(HttpServletResponse response, int status,
                                     String erro, String mensagem) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"status\":%d,\"erro\":\"%s\",\"mensagem\":\"%s\"}", status, erro, mensagem));
    }

    private static SecretKey chave(String segredo) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < TAMANHO_MINIMO_SEGREDO_BYTES) {
            throw new IllegalStateException(
                    "valky.jwt.secret (JWT_SECRET) precisa ter pelo menos "
                            + TAMANHO_MINIMO_SEGREDO_BYTES + " caracteres.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
