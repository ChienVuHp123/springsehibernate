package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lecturers")
@Data
public class Lecturer {
    @Id
    private int id;

    @Column(name = "DepartmentID")
    private int departmentID;

    @Column(name = "Name")
    private String name;

    @Column(name = "Email")
    private String email;

    @Column(name = "PhoneNumber")
    private int PhoneNumber;

}
