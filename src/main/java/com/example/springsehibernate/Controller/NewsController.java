package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.News;
import com.example.springsehibernate.Service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/home/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    // Phương thức để mở trang thêm tin tức mới
    @GetMapping("/add")
    public String showAddNewsForm(Model model) {
        model.addAttribute("news", new News());
        return "add-news"; // Trang view 'add-news.html' để thêm tin tức
    }

    // Phương thức POST để xử lý việc thêm tin tức mới
    @PostMapping("/add")
    public String addNews(@ModelAttribute News news, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                byte[] fileBytes = file.getBytes();
                news.setAttachment(fileBytes);
                news.setFileName(file.getOriginalFilename());
                news.setFileType(file.getContentType());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Could not upload file: " + e.getMessage());
                return "redirect:/home/news/add-news"; // Redirect back to the form if there's an error
            }
        }

        newsService.save(news);
        redirectAttributes.addFlashAttribute("message", "News added successfully!");

        return "redirect:/home";
    }


    // Phương thức POST để xử lý việc xóa tin tức
    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsService.deleteById(id); // Gọi service để xóa tin tức theo ID
        return "redirect:/home"; // Chuyển hướng người dùng trở lại trang chủ sau khi xóa
    }

    // Phương thức GET để hiển thị form chỉnh sửa với thông tin tin tức hiện có
    @GetMapping("/edit/{id}")
    public String showEditNewsForm(@PathVariable Long id, Model model) {
        News news = newsService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid news Id:" + id));
        model.addAttribute("news", news);
        return "edit-news";
    }

    // Phương thức POST để xử lý việc cập nhật tin tức
    @PostMapping("/edit/{id}")
    public String updateNews(@PathVariable Long id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                             @RequestParam(value = "removeAttachment", required = false) boolean removeAttachment,
                             RedirectAttributes redirectAttributes) {
        try {
            News existingNews = newsService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức với ID: " + id));

            // Cập nhật thông tin tin tức từ các tham số của form
            existingNews.setTitle(title);
            existingNews.setContent(content);
            // existingNews.setPublishDate(news.getPublishDate()); // Bỏ comment nếu bạn muốn cập nhật ngày xuất bản

            if (removeAttachment) {
                // Nếu người dùng muốn xóa file hiện tại
                existingNews.setAttachment(null);
                existingNews.setFileName(null);
                existingNews.setFileType(null);
            } else if (!attachment.isEmpty()) {
                // Nếu một file mới được tải lên và không phải là rỗng
                byte[] fileBytes = attachment.getBytes();
                existingNews.setAttachment(fileBytes);
                existingNews.setFileName(attachment.getOriginalFilename());
                existingNews.setFileType(attachment.getContentType());
            }

            newsService.save(existingNews);
            redirectAttributes.addFlashAttribute("message", "Tin tức đã được cập nhật thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi tải file lên: " + e.getMessage());
            return "redirect:/edit/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/edit/" + id;
        }

        return "redirect:/home";
    }




    // Phương thức GET để hiển thị trang chi tiết tin tức
    @GetMapping("/{id}")
    public String viewNewsDetail(@PathVariable Long id, Model model) {
        News news = newsService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức với ID: " + id));
        model.addAttribute("news", news);
        return "news-detail"; // Tên file view là news-detail.html
    }

    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        News news = newsService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin tức với ID: " + id));
        if (news != null && news.getAttachment() != null) {
            // Thiết lập các thông tin cần thiết cho response
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + news.getFileName() + "\"");
            response.getOutputStream().write(news.getAttachment());
            response.flushBuffer();
        }
    }

}
