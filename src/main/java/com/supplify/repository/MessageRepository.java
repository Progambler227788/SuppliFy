package com.supplify.repository;

import com.supplify.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	List<Message> findBySenderIdOrReceiverId(UUID senderId, UUID receiverId);
	List<Message> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

}

