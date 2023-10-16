package com.example.springsehibernate.Repository;

import com.example.springsehibernate.Entity.Message;
import com.example.springsehibernate.Entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdAndStatusEnum(Long receiverId, MessageStatus status);

    List<Message> findAllBySenderId(Long senderId);

    List<Message> findAllByReceiverId(Long senderId);
}
