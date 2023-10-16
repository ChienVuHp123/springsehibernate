package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.MessageStatus;
import com.example.springsehibernate.Entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.LecturerID = :LecturerID")
    List<Student> findByLecturerID(@Param("LecturerID") Long LecturerID);

    Student getStudentByID(Long ID);

    Student findStudentByID(Long ID);

    @Query("SELECT s FROM Student s WHERE s.LecturerID = :lecturerID")
    Page<Student> findByLecturerID(@Param("lecturerID") Long lecturerID, Pageable pageable);

    Optional<Student> findByID(Long ID);


//    List<Student> findByMessage_StatusEnum(MessageStatus status);
//
//    List<Student> findByLecturerIDAndMessages_StatusEnum(long lecturerId, MessageStatus status);
}
