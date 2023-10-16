package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.*;
import com.example.springsehibernate.Repository.ConfirmTableRepository;
import com.example.springsehibernate.Repository.MessageRepository;
import com.example.springsehibernate.Repository.StudentRepository;
import com.example.springsehibernate.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/bo-mon")
public class BoMonController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConfirmTableRepository confirmTableRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ConfirmTableService confirmTableService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/list/{lecturerID}")
    public String getList(@PathVariable("lecturerID") Long lecturerID, Model model) {

        List<Student> students = studentRepository.findByLecturerID(lecturerID);
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

            if (message.getStatusEnum() == MessageStatus.ACCEPTED) {
                // Lấy senderId từ message
                Long senderId = message.getSenderId();

                // Tìm tất cả sinh viên có lecturerID = senderId
                List<Student> studentsToConfirm = studentService.findByLecturerID(senderId);


                // Thêm các sinh viên vào ConfirmTable và cập nhật thông tin cho sinh viên
                for (Student student : studentsToConfirm) {
//                    student.setConfirmTable(confirmTable);
                    ConfirmTable confirmTable = new ConfirmTable();
                    confirmTable.setLecturerId(student.getLecturerID());
                    confirmTable.setStudent(student);
                    confirmTable.setDepartmentId(currentUser.getUserID());
                    confirmTableRepository.save(confirmTable);
                }

            }

            Long senderId = message.getSenderId();
            // Gửi thông báo cho user Giảng viên sau khi cập nhật trạng thái
            String notificationContent = "Danh sách của bạn đã được chấp nhận.";
            notificationService.sendNotificationToLecturer(notificationContent, senderId);
        }
        return "redirect:/bo-mon";
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
        return "redirect:/bo-mon";
    }

    @GetMapping("/send-confirm-list")
    public String getSendConfirmList(Model model,Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(senderDetails.getUsername());

        List<ConfirmTable> confirmedStudents = confirmTableRepository.findByDepartmentId(currentUser.getUserID());
        model.addAttribute("confirmedStudents", confirmedStudents);
        return "send-confirm-list";
    }


    @PostMapping("/send-confirm-list")
    public String postSendConfirmList(@RequestParam("receiverId") Long receiverId,
                                      @RequestParam("messageContent") String messageContent,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User senderUser = userService.findByUsername(senderDetails.getUsername());

        Message message = new Message();
        message.setSenderId(senderUser.getUserID());
        message.setSenderName(senderUser.getRealname());
        message.setReceiverId(receiverId);
        message.setMessageContent(messageContent);
        message.setSentAt(LocalDateTime.now()); // Thiết lập thời gian gửi
        messageService.saveMessage(message);

        // Thêm flash message để thông báo gửi tin nhắn thành công
        redirectAttributes.addFlashAttribute("successMessage", "Danh sách đã được gửi thành công!");

        return "redirect:/bo-mon/send-confirm-list";
    }
}
