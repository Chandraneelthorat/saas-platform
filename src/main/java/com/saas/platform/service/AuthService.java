package com.saas.platform.service;

import com.saas.platform.dto.AuthResponse;
import com.saas.platform.dto.LoginRequest;
import com.saas.platform.dto.RegisterRequest;
import com.saas.platform.model.Tenant;
import com.saas.platform.model.User;
import com.saas.platform.repository.TenantRepository;
import com.saas.platform.repository.UserRepository;
import com.saas.platform.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        String slug = request.getTenantName()
                .toLowerCase()
                .replaceAll("\\s+", "-");

        // Find existing tenant or create a new one
        Tenant tenant = tenantRepository.findByName(request.getTenantName())
                .orElseGet(() -> {
                    Tenant newTenant = Tenant.builder()
                            .name(request.getTenantName())
                            .slug(slug)
                            .active(true)
                            .build();
                    return tenantRepository.save(newTenant);
                });

        // First user in a tenant = ADMIN, everyone else = MEMBER
        boolean isFirstUser = !userRepository.existsByTenantId(tenant.getId());
        User.Role role = isFirstUser ? User.Role.ADMIN : User.Role.MEMBER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .tenant(tenant)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantName(tenant.getName())
                .build();
    }
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantName(user.getTenant().getName())
                .build();
    }
}