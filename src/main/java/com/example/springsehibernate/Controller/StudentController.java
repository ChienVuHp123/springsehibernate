package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.MessageRepository;
import com.example.springsehibernate.Repository.StudentRepository;
import com.example.springsehibernate.Service.StudentService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private UserService userService;

    // Tải người dùng hiện tại dựa trên thông tin xác thực
    private User getCurrentUser(Authentication authentication) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername());
        }
        return null;
    }

    @GetMapping("/students")
    public String listStudents(Model model,
                               Authentication authentication,
                               @RequestParam(name="page", defaultValue="0") int page) {
        if(authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            List<Student> students;
            if (user != null) {
                Long lecturerId = user.getUserID();
                Lecturer lecturer = lecturerRepository.findById(lecturerId.intValue());

//                students = studentService.findByLecturerID(user.getUserID());
                Page<Student> studentsPage = studentService.findByLecturerID(lecturerId, PageRequest.of(page, 5)); // 10 là số lượng sinh viên trên mỗi trang

                List<Message> messages = messageRepository.findAllBySenderId((long) lecturer.getDepartmentID());
                boolean hasExceptedMessage = messages.stream().anyMatch(message -> MessageStatus.ACCEPTED.equals(message.getStatusEnum()));

                model.addAttribute("studentsPage", studentsPage);
                model.addAttribute("showColumns", hasExceptedMessage);
            } else {
                students = Collections.emptyList();
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            model.addAttribute("userRoles", authorities);
//            model.addAttribute("students", students);
            model.addAttribute("newStudent", new Student());
            return "students";
        }

        return "redirect:/login";
    }

    @PostMapping("/students/add")
    public String addStudent(@ModelAttribute Student student,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        if(authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            if (user != null) {
                student.setLecturerID(user.getUserID());
            }
        }

        studentRepository.save(student);
        redirectAttributes.addFlashAttribute("message", "Thêm dữ liệu thành công!");
        return "redirect:/students";
    }

    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa học sinh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa học sinh!");
        }
        return "redirect:/students";
    }

//    @GetMapping("students/edit/{id}")
//    public String editStudent(@PathVariable("id") Long id, Model model) {
//        Student student = studentService.getStudentById(id);
//        model.addAttribute("student", student);
//        return "edit-student"; // name of the thymeleaf template for editing student
//    }



//    @GetMapping("students/edit/{studentId}")
//    public String editStudent(@PathVariable("studentId") Long studentId, Model model) {
//        try {
//            Student student = studentRepository.getStudentByID(studentId);
//
//            model.addAttribute("student", student);
//            model.addAttribute("pageTitle", "Edit Tutorial (ID: " + student.getID() + ")");
//
//            return "student_form";
//        } catch (Exception e) {
//            throw new RuntimeException("dm minh chien");
//        }
//    }

    @PostMapping("/students/update")
    public String updateStudent(Student student, Authentication authentication, RedirectAttributes redirectAttrs) {
        try {
            User currentUser = getCurrentUser(authentication);
            student.setLecturerID(currentUser.getUserID());
            studentRepository.save(student);

            // Thêm thông báo thành công vào redirect attributes
            redirectAttrs.addFlashAttribute("updateSuccess", true);
        } catch (Exception e) {
            // Bắt và xử lý ngoại lệ
            // Thêm thông báo thất bại vào redirect attributes
            redirectAttrs.addFlashAttribute("updateFailed", true);
        }

        return "redirect:/students";
    }

}

