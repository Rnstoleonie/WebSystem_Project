package com.gradeportal.config;

import com.gradeportal.entity.User;
import com.gradeportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return UserPrincipal.create(user);
    }
    
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return UserPrincipal.create(user);
    }
    
    public static class UserPrincipal implements UserDetails {
        private Long id;
        private String username;
        private String email;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;
        private User.UserRole role;
        private User.UserStatus status;
        
        public UserPrincipal(Long id, String username, String email, String password, 
                           Collection<? extends GrantedAuthority> authorities, 
                           User.UserRole role, User.UserStatus status) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
            this.role = role;
            this.status = status;
        }
        
        public static UserPrincipal create(User user) {
            List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
            
            return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getUsername(), // Using username as email
                user.getPassword(),
                authorities,
                user.getRole(),
                user.getStatus()
            );
        }
        
        public Long getId() {
            return id;
        }
        
        public User.UserRole getRole() {
            return role;
        }
        
        public User.UserStatus getStatus() {
            return status;
        }
        
        @Override
        public String getUsername() {
            return username;
        }
        
        @Override
        public String getPassword() {
            return password;
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return this.status == User.UserStatus.APPROVED;
        }
    }
}
