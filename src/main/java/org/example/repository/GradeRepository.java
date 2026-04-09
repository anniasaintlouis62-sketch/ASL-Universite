package org.example.repository;

import org.example.model.Grade;
import org.example.model.Student;
import org.example.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(Student student);

    List<Grade> findBySubject(Subject subject);

    List<Grade> findByStudentAndAcademicYear(Student student, String academicYear);

    List<Grade> findAllByOrderByAcademicYearDescSubject_NameAsc();

    @Query("select distinct g.academicYear from Grade g where g.academicYear is not null and g.academicYear <> '' order by g.academicYear desc")
    List<String> findDistinctAcademicYears();
}
