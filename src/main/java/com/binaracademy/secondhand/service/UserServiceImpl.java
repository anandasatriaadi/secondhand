package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ResponseUserDto;
import com.binaracademy.secondhand.dto.UploadUserDto;
import com.binaracademy.secondhand.model.User;
import com.binaracademy.secondhand.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseUserDto saveUser(UploadUserDto userDto) {
        log.info("Saving User");
        if (userDto.getPassword() != null && userDto.getEmail() != null && userDto.getFullName() != null) {
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            user.setFullName(userDto.getFullName());

            User result = userRepository.save(user);
            return modelMapper.map(result, ResponseUserDto.class);
        } else {
            return null;
        }
    }

    @Override
    public ResponseUserDto getUser(String email) {
        log.info("Getting User");
        User result = userRepository.findByEmail(email);
        return modelMapper.map(result, ResponseUserDto.class);
    }

    @Override
    public List<ResponseUserDto> getAllUsers() {
        log.info("Getting All Users");
        List<User> result = userRepository.findAll();
        return modelMapper.map(result, new TypeToken<List<ResponseUserDto>>() {}.getType());
    }

    @Override
    public boolean checkUser(String email) {
        User result = userRepository.findByEmail(email);
        return (result.getFullName() != null && result.getPhoneNumber() != null && result.getAddress() != null && result.getCity() != null);
    }

    // ========================================================================
    //                       USER DETAILS SERVICE
    // ========================================================================
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User {} found in the database", email);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
