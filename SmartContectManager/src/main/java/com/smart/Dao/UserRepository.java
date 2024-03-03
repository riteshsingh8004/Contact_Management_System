package com.smart.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	   
	@Query("select u from User u where u.email = :email")
	   public User getUserByName(@Param("email") String email);
}
