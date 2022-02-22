package com.example.news.service;

import com.example.news.model.UserInfo;
import com.example.news.model.UserInfoDto;
import com.example.news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserInfo loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException((email)));
    }

    /**
     * 사용자 추가
     *
     * @param infoDto
     * @return
     */
    public UserInfo addUser(UserInfoDto infoDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        infoDto.setPassword(encoder.encode(infoDto.getPassword()));
        UserInfo userInfo = UserInfo.builder()
                .email(infoDto.getEmail())
                .auth(infoDto.getAuth())
                .password(infoDto.getPassword()).build();
        return userRepository.save(userInfo);
    }

    /**
     * 로그인된 사용자 정보 반환
     *
     * @return
     */
    public UserInfo getSignedUserInfo() {
        Object pr = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userInfo = (UserInfo) pr;
        return userInfo;
    }
}
