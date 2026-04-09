package org.example.service;

import org.example.dto.BulletinData;
import org.example.dto.BulletinLine;
import org.example.model.Grade;
import org.example.model.Student;
import org.example.repository.GradeRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BulletinService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;

    public BulletinService(StudentRepository studentRepository, GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
    }

    public BulletinData getBulletinByStudentId(String studentId) {
        List<String> years = getAvailableAcademicYears(studentId);
        String targetYear = years.isEmpty() ? "N/A" : years.get(0);
        return getBulletinByStudentIdAndYear(studentId, targetYear);
    }

    public BulletinData getBulletinByStudentIdAndYear(String studentId, String academicYear) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Etudiant introuvable : " + studentId));

        List<Grade> grades = gradeRepository.findByStudentAndAcademicYear(student, academicYear);
        List<BulletinLine> lines = grades.stream()
                .map(g -> new BulletinLine(
                        g.getSubject().getName(),
                        g.getSubject().getProfessor().getFullName(),
                        g.getSubject().getCoefficient(),
                        g.getValue()
                ))
                .toList();

        double weightedSum = lines.stream().mapToDouble(line -> line.grade() * line.coefficient()).sum();
        double totalCoef = lines.stream().mapToDouble(BulletinLine::coefficient).sum();
        double average = totalCoef == 0 ? 0 : weightedSum / totalCoef;

        return new BulletinData(
                student.getStudentId(),
                student.getFullName(),
                academicYear,
                lines,
                average,
                mentionFromAverage(average)
        );
    }

    public List<String> getAvailableAcademicYears(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Etudiant introuvable : " + studentId));

        return gradeRepository.findByStudent(student).stream()
                .map(Grade::getAcademicYear)
                .filter(y -> y != null && !y.isBlank())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    private String mentionFromAverage(double average) {
        if (average >= 16) return "Tres Bien";
        if (average >= 14) return "Bien";
        if (average >= 12) return "Assez Bien";
        if (average >= 10) return "Passable";
        return "Insuffisant";
    }
}
