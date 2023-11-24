package com.example.springsehibernate.Controller;

import com.example.springsehibernate.DTO.UserRegistrationDto;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bo-mon")
public class BoMonApiController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User registeredUser = userService.registerNewUser(registrationDto);
            // Trả về response phù hợp, có thể chỉ là thông báo hoặc thông tin người dùng đã đăng ký
            return ResponseEntity.ok("Người dùng đã được đăng ký thành công với tên đăng nhập: " + registeredUser.getUsername());
        } catch (IllegalStateException e) {
            // Trả về thông báo lỗi nếu đăng ký không thành công (ví dụ: tên người dùng đã tồn tại)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.internalServerError().body("Có lỗi xảy ra trong quá trình đăng ký.");
        }
    }

    @GetMapping("/checkOwnerId")
    public ResponseEntity<?> checkOwnerId(@RequestParam("ownerId") Long ownerId) {
        boolean exists = userService.checkOwnerIdExists(ownerId);
        return ResponseEntity.ok(exists);
    }
}
