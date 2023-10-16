package com.example.springsehibernate.Controller;

import com.example.springsehibernate.Entity.Student;
import com.example.springsehibernate.Repository.StudentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class StudentApiController {
    @Autowired
    private StudentRepository studentRepository;
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


}

