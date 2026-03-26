package com.distributed_streaming_platform.auth_service.security;


import com.distributed_streaming_platform.auth_service.entity.User;
import com.distributed_streaming_platform.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Setter
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {


        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new  UsernameNotFoundException("User not found "));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
