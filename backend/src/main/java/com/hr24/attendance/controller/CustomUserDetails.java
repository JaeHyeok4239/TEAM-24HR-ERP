package com.hr24.attendance.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

// Spring Security 인증 객체에 employeeId를 담아 권한 검증에 사용하게 하는 클래스
public class CustomUserDetails implements UserDetails {

    private final Long employeeId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long employeeId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 아래 메서드들은 기본값 true로 설정
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}