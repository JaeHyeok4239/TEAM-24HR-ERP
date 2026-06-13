package com.hr24.auth.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.RoleRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        
        if (!jwtProvider.validateToken(token)) {
			filterChain.doFilter(request, response);
			return;
		}
        
        String loginId = jwtProvider.getLoginId(token);
        
        User user = userRepository.findByLoginId(loginId)
        		.orElse(null);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        List<UserRole> userRoles =
                userRoleRepository.findByEmployeeId(user.getEmployeeId());

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        List<String> roles = roleRepository.findByRoleIdIn(roleIds)
                .stream()
                .map(Role::getRoleCode)
                .toList();

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getLoginId(),
                        null,
                        authorities
                );
        
        SecurityContextHolder.getContext()
        		.setAuthentication(authentication);
        
        filterChain.doFilter(request, response);
    }
}