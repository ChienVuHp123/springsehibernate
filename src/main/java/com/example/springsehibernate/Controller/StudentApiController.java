package com.example.springsehibernate.Controller;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;
import com.example.springsehibernate.Config.DropboxConfig;
import com.example.springsehibernate.Entity.AcademicYearUtil;
import com.example.springsehibernate.Entity.Lecturer;
import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Entity.User;
import com.example.springsehibernate.Repository.LecturerRepository;
import com.example.springsehibernate.Repository.StudentRepository;
import com.example.springsehibernate.Service.DropboxService;
import com.example.springsehibernate.Service.StudentService;
import com.example.springsehibernate.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class StudentApiController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DropboxService dropboxService;

    @Autowired
    private DropboxConfig dropboxConfig;

    private User getCurrentUser(Authentication authentication) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.findByUsername(userDetails.getUsername());
        }
        return null;
    }
    // Các phương thức xử lý API

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id)  {
        Optional<Student> optionalStudent = studentRepository.findByID(id);
        if (optionalStudent.isPresent()) {
            return new ResponseEntity<>(optionalStudent.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable Long id,
            @RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
            @RequestParam Map<String, String> formData,
            Authentication authentication,
            @RequestParam(name="academicYear", required=false) String chosenAcademicYear,
            @RequestParam(name="semester", required=false) Integer chosenSemester) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Student> optionalStudent = studentRepository.findById(id);
            if (!optionalStudent.isPresent()) {
                response.put("status", "failed");
                response.put("message", "Student not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Lấy sinh viên từ cơ sở dữ liệu và cập nhật thông tin
            Student existingStudent = optionalStudent.get();

            // Cập nhật thông tin từ đối tượng student được gửi từ form
            Long studentId = Long.parseLong(formData.get("IdEdit"));
            existingStudent.setStudentID(studentId);
            existingStudent.setName(formData.get("NameEdit"));
            // Chuyển đổi ngày sinh từ String sang LocalDate
            String dobString = formData.get("BirthEdit");
            LocalDate dateOfBirth = LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
            existingStudent.setDateOfBirth(dateOfBirth);
//            existingStudent.setThesistopics(student.getThesistopics());
//            existingStudent.setNewTopics(student.getNewTopics());
//            existingStudent.setDtbc(student.getDtbc());
//            existingStudent.setNamelecturer(student.getNamelecturer());
//            existingStudent.setNamesecondlecturer(student.getNamesecondlecturer());
//            if (student.getNamesecondlecturer() != null) {
//                Lecturer SecondLecturer = lecturerRepository.findByName(student.getNamesecondlecturer());
//                if (SecondLecturer != null) {
//                    existingStudent.setSecondLecturerId(SecondLecturer.getId());
//                } else {
//                    existingStudent.setSecondLecturerId(null); // Xử lý trường hợp không tìm thấy giảng viên
//                }
//            } else {
//                existingStudent.setSecondLecturerId(null); // Xử lý khi student.getNamesecondlecturer() là null
//            }
//
//            existingStudent.setUniversity(student.getUniversity());
//            existingStudent.setLecturerReviewer(student.getLecturerReviewer());
//            existingStudent.setLecturerReviewerWorkplace(student.getLecturerReviewerWorkplace());
//            existingStudent.setSecondLecturerReviewer(student.getSecondLecturerReviewer());
//            existingStudent.setSecondLecturerReviewerWorkplace(student.getSecondLecturerReviewerWorkplace());
//            existingStudent.setStatus(student.getStatus());



            // Xử lý tập tin tải lên
            if (fileUpload != null && !fileUpload.isEmpty()) {
                String filePath = uploadFileToDropbox(fileUpload);
                System.out.println(filePath);
                existingStudent.setFilePath(filePath);
            } else {
                System.out.println("file is null");
            }

            User currentUser = getCurrentUser(authentication);
            Lecturer lecturerEntity = lecturerRepository.findById(currentUser.getOwnerId()).orElse(null);
            existingStudent.setLecturer(lecturerEntity);
            // Đặt năm học và học kỳ dựa trên sự lựa chọn của người dùng hoặc thời gian hiện tại
            if(chosenAcademicYear != null) {
                existingStudent.setAcademicYear(chosenAcademicYear);
            } else {
                existingStudent.setAcademicYear(AcademicYearUtil.getCurrentAcademicYear());
            }

            if(chosenSemester != null) {
                existingStudent.setSemester(chosenSemester);
            } else {
                existingStudent.setSemester(AcademicYearUtil.getCurrentSemester());
            }
            studentRepository.save(existingStudent);

            response.put("status", "success");
            response.put("message", "Student updated successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Bắt và xử lý ngoại lệ
            response.put("status", "failed");
            response.put("message", "Error updating student.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String uploadFileToDropbox(MultipartFile file) throws Exception {
        // Làm mới accessToken nếu cần
        String accessToken = getUpdatedAccessToken(dropboxConfig.getRefreshToken());
        dropboxConfig.setAccessToken(accessToken);
        DbxRequestConfig config = DbxRequestConfig.newBuilder("uet-project-storage").build();
        System.out.println(config);

        DbxClientV2 client = new DbxClientV2(config, dropboxConfig.getAccessToken());
        System.out.println(client);

        try {
            FullAccount account = client.users().getCurrentAccount();
            System.out.println("Token is valid. User's account: " + account.getName().getDisplayName());
        } catch (Exception e) {
            // Handle the exception if the token is invalid or has expired
            System.out.println("Token is invalid or has expired.");
            e.printStackTrace();
        }

        try (InputStream in = file.getInputStream()) {
            String dropboxPath = "/home/uetStorage/" + file.getOriginalFilename(); // Specify the Dropbox path
            System.out.println(dropboxPath);
            FileMetadata metadata = client.files().uploadBuilder(dropboxPath)
                    .uploadAndFinish(in);
            System.out.println("File uploaded successfully. Dropbox path: " + metadata.getPathDisplay());
            return metadata.getPathLower();
        } catch (Exception e) {
            // Handle the exception if the upload fails
            System.out.println("File upload failed.");
            e.printStackTrace();
            return "0";
        }
    }

    private String getUpdatedAccessToken(String refreshToken) throws Exception {
        String clientId = dropboxConfig.getAppKey(); // Thay thế với Client ID của ứng dụng Dropbox
        String clientSecret = dropboxConfig.getAppSecret(); // Thay thế với Client Secret của ứng dụng Dropbox

        // Tạo URL để yêu cầu làm mới token
        URL url = new URL("https://api.dropbox.com/oauth2/token");
        String params = "grant_type=refresh_token&refresh_token=" + refreshToken +
                "&client_id=" + clientId + "&client_secret=" + clientSecret;
        System.out.println(params);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

        try {
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Đọc phản hồi từ Dropbox
                String response = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject jsonResponse = new JSONObject(response);

                // Kiểm tra và lấy access_token mới
                if (jsonResponse.has("access_token")) {
                    return jsonResponse.getString("access_token");
                }
            } else {
                // Xử lý trường hợp phản hồi không thành công
                String errorResponse = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Error response: " + errorResponse);
                throw new Exception("Failed to refresh access token with response code: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
        throw new Exception("Failed to refresh access token");
    }
}

