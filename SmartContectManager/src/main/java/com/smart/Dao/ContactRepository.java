package com.smart.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
  
	@Query("from Contact as c where c.user.uid =:userId")
	//cureent page
	// page per record
	public Page<Contact> findContactByuser(@Param("userId")int userId,Pageable pageable);
	
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
