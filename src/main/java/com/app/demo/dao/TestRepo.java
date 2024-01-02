package com.app.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.bean.Test;

public interface TestRepo extends JpaRepository<Test, Long> 
{
	List<Test> findByUsername(String username);
}
