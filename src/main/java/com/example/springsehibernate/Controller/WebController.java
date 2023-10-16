package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.Message;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.MessageRepository;
import com.example.springsehibernate.Service.MessageService;
import com.example.springsehibernate.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    MessageRepository messageRepository;

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login.html");
        return modelAndView;
    }


    @GetMapping("/giang-vien")
    public ModelAndView homeGiangVien() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("giang-vien.html");
        return modelAndView;
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("receiverId") Long receiverId,
                              @RequestParam("messageContent") String messageContent,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        UserDetails senderDetails = (UserDetails) authentication.getPrincipal();
        User sender = userService.findByUsername(senderDetails.getUsername());

        Message message = new Message();
        message.setSenderId(sender.getUserID());
        message.setSenderName(sender.getRealname());
        message.setReceiverId(receiverId);
        message.setMessageContent(messageContent);
        message.setSentAt(LocalDateTime.now()); // Thiết lập thời gian gửi
        messageService.saveMessage(message);

        // Thêm flash message để thông báo gửi tin nhắn thành công
        redirectAttributes.addFlashAttribute("successMessage", "Danh sách đã được gửi thành công!");

        return "redirect:/students";
    }



    @GetMapping("/bo-mon")
    public ModelAndView homeBoMon(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        ModelAndView modelAndView = new ModelAndView();
        List<Message> messages = messageService.getMessagesForUser(user.getUserID());
        model.addAttribute("messages", messages);
        modelAndView.setViewName("bo-mon.html");
        return modelAndView;
    }

    @GetMapping("/khoa")
    public ModelAndView homeKhoa(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        ModelAndView modelAndView = new ModelAndView();
        List<Message> messages = messageService.getMessagesForUser(user.getUserID());
        model.addAttribute("messages", messages);
        modelAndView.setViewName("khoa.html");
        return modelAndView;
    }

    @GetMapping("/hello")
    public ModelAndView hello() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("hello.html");
        return modelAndView;
    }

    @GetMapping("/notify")
    public ModelAndView NotifyView(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        modelAndView.addObject("accountId", user.getUserID());
        modelAndView.setViewName("notify.html");

        return modelAndView;
    }
    @GetMapping(value = {"/", "/home"})
    public ModelAndView yourMethod(Model model, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        // Lấy danh sách roles
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("accountId", user.getUserID());
        model.addAttribute("roles", roles);  // thêm dòng này

        modelAndView.setViewName("home.html");
        return modelAndView;
    }




}
