package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UserRegisterDto;
import com.binaracademy.secondhand.dto.UserUploadDto;
import com.binaracademy.secondhand.model.User;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.util.CloudinaryUtil;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public User saveUser(UserRegisterDto userDto) {
        log.info("Saving User");
        if (userDto.getPassword() != null && userDto.getEmail() != null && userDto.getFullName() != null) {
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            user.setFullName(userDto.getFullName());

            return userRepository.save(user);
        } else {
            return null;
        }
    }

    @Override
    public User updateUser(String email, UserUploadDto userDto) {
        log.info("Updating User");

        User user = userRepository.findByEmail(email);
        user.setFullName(userDto.getFullName());
        user.setAddress(userDto.getAddress());
        user.setCity(userDto.getCity());
        user.setPhoneNumber(userDto.getPhoneNumber());

        try {
            File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + userDto.getImage().getOriginalFilename());
            userDto.getImage().transferTo(convertFile);
            String imageUrl = (String) CloudinaryUtil.cloudinary.uploader().upload(convertFile, ObjectUtils.emptyMap()).get("url");

            user.setImageUrl(imageUrl);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        return userRepository.save(user);
    }

    @Override
    public User getUser(String email) {
        log.info("Getting User");
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserId(Long id) {
        log.info("Getting User");
        Optional<User> user = userRepository.findById(id);
        return user.isPresent() ? user.get() : null;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Getting All Users");
        return userRepository.findAll();
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
