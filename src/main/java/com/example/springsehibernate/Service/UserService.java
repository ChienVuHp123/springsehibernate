package com.example.springsehibernate.Service;

import com.example.springsehibernate.Entity.CustomUserDetails;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }

    public User findByUsername(String username) {
        // Sử dụng phương thức findByUsername từ repository
        return userRepository.findByUsername(username);
        // Nếu bạn muốn xử lý trường hợp không tìm thấy tài khoản,
        // bạn có thể thay thế orElse(null) bằng cách thức khác.
    }

    // JWTAuthenticationFilter sẽ sử dụng hàm này
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return new CustomUserDetails(user);
    }

}