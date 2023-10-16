package com.example.springsehibernate;

import com.example.springsehibernate.Entity.RoleEnum;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringSeHibernateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSeHibernateApplication.class, args);
    }
//    @Autowired
//    StudentRepository studentRepository;
//    @Override
//    public void run(String... args) throws Exception {
//        // Khi chương trình chạy
//        // Insert vào csdl một user.
//        students students = new students();
//        students.setID(19021111L);
//        students.setEmail("19021111@vnu.edu.vn");
//        students.setName("Nguyen Van A");
//        System.out.println(students);
//    }
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

//    @Override
//    public void run(String... args) throws Exception {
//        // Khi chương trình chạy
//        // Insert vào csdl một user.
//        User user = new User();
//        user.setUsername("BoMon");
//        user.setPassword(passwordEncoder.encode("loda"));
//        user.setOwnerID(126);
//        user.setRole(RoleEnum.BoMon);
//        userRepository.save(user);
//        System.out.println(user);
//    }

}
