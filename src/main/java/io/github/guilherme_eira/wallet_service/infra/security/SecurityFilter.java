package io.github.guilherme_eira.wallet_service.infra.security;

import io.github.guilherme_eira.wallet_service.application.port.out.TokenProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoverToken(request);

        if (token != null) {
            try {
                var email = tokenProvider.getSubject(token);
                var optionalUser = repository.findByEmail(email);

                if (optionalUser.isEmpty()) {
                    log.warn("Autenticação solicitada para email inexistente: {}", email);
                } else {
                    var user = optionalUser.get();

                    if (!user.isVerified()) {
                        log.warn("Autenticação negada. Usuário não verificado: {}", email);

                    } else {
                        var role = tokenProvider.getRole(token);
                        var authorities = (role != null)
                                ? List.of(new SimpleGrantedAuthority(role))
                                : Collections.<SimpleGrantedAuthority>emptyList();

                        var principal = new UserPrincipal(user, authorities);
                        var authToken = new UsernamePasswordAuthenticationToken(
                                principal, null, authorities
                        );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authToken);
                    }
                }

            } catch (Exception ex) {
                log.error("Erro ao processar token JWT", ex);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
