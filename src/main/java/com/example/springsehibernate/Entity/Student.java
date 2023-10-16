package com.example.springsehibernate.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    private Long ID;

    @Column(name = "LecturerID")
    private Long LecturerID;

    @Column(name = "Name")
    private String Name;

    @Column(name = "Email")
    private String Email;

    @Column(name = "PhoneNumber")
    private String PhoneNumber;

    @Column(name = "DateOfBirth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate DateOfBirth;

    @Column(name = "Thesistopics")
    private String Thesistopics;

    @Column(name = "dtbc")
    private float dtbc;

    @Column(name = "university")
    private String university;

    @Column(name = "namelecturer")
    private String namelecturer;

    @Column(name = "LecturerReviewer")
    private String LecturerReviewer; // Giảng viên phản biện

    @Column(name = "LecturerReviewerWorkplace")
    private String LecturerReviewerWorkplace; // Nơi công tác của giảng viên phản biện

    private String academicYear;

    @JsonIgnore
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ConfirmTable confirmTable;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

}
