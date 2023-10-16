package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "confirm_table")
public class ConfirmTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "lecturer_id")
    private Long lecturerId;

    @Column(name = "department_id")
    private Long departmentId;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "ID") // Chỉ định tên trường tương ứng
    private Student student;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @Override
    public String toString() {
        return "ConfirmTable{" +
                "Id=" + Id +
                ", lecturerId=" + lecturerId +
                ", departmentId=" + departmentId +
                // Không bao gồm student và message trong toString()
                '}';
    }

}

