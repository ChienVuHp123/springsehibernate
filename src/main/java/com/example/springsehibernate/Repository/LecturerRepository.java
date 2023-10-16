package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Lecturer findById(int senderId);
}
