package org.example.repository;

import org.example.model.Professor;
import org.example.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByProfessor(Professor professor);
}
