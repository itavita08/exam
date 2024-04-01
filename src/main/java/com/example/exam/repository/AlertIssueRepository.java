package com.example.exam.repository;

import com.example.exam.model.entity.AlertIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertIssueRepository extends JpaRepository<AlertIssue, Integer> {
}
