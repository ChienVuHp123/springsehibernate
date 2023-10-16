package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.MessageRepository;
import com.example.springsehibernate.Repository.StudentRepository;
import com.example.springsehibernate.Service.NotificationService;
import com.example.springsehibernate.Service.StudentService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/khoa")
public class KhoaController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;

    @GetMapping("/list/{departmentID}")
    public String getList(@PathVariable("departmentID") Long departmentID, Model model) {

        List<Student> students = studentRepository.findByLecturerID(departmentID);
        model.addAttribute("students", students);
        return "listView"; // "listView" là tên file view (Thymeleaf template) bạn muốn render
    }

    @PostMapping("/list/confirm/{messageId}")
    public String confirmList(@PathVariable("messageId") long messageId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(senderDetails.getUsername());


        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatusEnum(MessageStatus.ACCEPTED);
            messageRepository.save(message);

//            if (message.getStatusEnum() == MessageStatus.ACCEPTED) {
//                // Lấy senderId từ message
//                Long senderId = message.getSenderId();
//
//                // Tìm tất cả sinh viên có lecturerID = senderId
//                List<Student> studentsToConfirm = studentService.findByLecturerID(senderId);
//
//
//                // Thêm các sinh viên vào ConfirmTable và cập nhật thông tin cho sinh viên
//                for (Student student : studentsToConfirm) {
////                    student.setConfirmTable(confirmTable);
//                    ConfirmTable confirmTable = new ConfirmTable();
//                    confirmTable.setLecturerId(student.getLecturerID());
//                    confirmTable.setStudent(student);
//                    confirmTable.setDepartmentId(currentUser.getUserID());
//                    confirmTableRepository.save(confirmTable);
//                }

           // }

            Long senderId = message.getSenderId();
            // Gửi thông báo cho user Bộ Môn sau khi cập nhật trạng thái
            String notificationContent = "Danh sách của bạn đã được chấp nhận.";
            notificationService.sendNotificationToLecturer(notificationContent, senderId);
        }
        return "redirect:/khoa";
    }


    @PostMapping("/list/reject/{messageId}")
    public String rejectList(@PathVariable("messageId") long messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            message.setStatusEnum(MessageStatus.REJECTED);
            messageRepository.save(message);
            // Lấy sender_id của người gửi thông báo
            Long senderId = message.getSenderId();
            // Gửi thông báo cho user Giảng viên sau khi cập nhật trạng thái
            String notificationContent = "Danh sách của bạn đã bị từ chối.";
            notificationService.sendNotificationToLecturer(notificationContent, senderId);
        }
        return "redirect:/khoa";
    }
}
