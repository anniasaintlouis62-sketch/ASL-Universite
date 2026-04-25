package org.example.bootstrap;

import org.example.model.Grade;
import org.example.model.Professor;
import org.example.model.Student;
import org.example.model.Subject;
import org.example.repository.GradeRepository;
import org.example.repository.ProfessorRepository;
import org.example.repository.StudentRepository;
import org.example.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;

    public DataInitializer(StudentRepository studentRepository,
                           ProfessorRepository professorRepository,
                           SubjectRepository subjectRepository,
                           GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("Starting Data Initialization...");
            if (studentRepository.count() > 0) {
                System.out.println("Data already initialized. Skipping.");
                return;
            }

            Professor p1 = professorRepository.save(new Professor("Dr Marie Joseph", "marie.joseph@universite.ht"));
            Professor p2 = professorRepository.save(new Professor("Dr Jean Pierre", "jean.pierre@universite.ht"));

            Subject s1 = subjectRepository.save(new Subject("Programmation Java", 4, p1));
            Subject s2 = subjectRepository.save(new Subject("Base de Donnees", 3, p2));
            Subject s3 = subjectRepository.save(new Subject("Reseaux", 2, p2));

            Student st1 = studentRepository.save(new Student("ETU001", "Annia Saint-Louis"));
            Student st2 = studentRepository.save(new Student("ETU002", "Paul Michel"));

            gradeRepository.save(new Grade(st1, s1, 16, "2024-2025"));
            gradeRepository.save(new Grade(st1, s2, 14, "2024-2025"));
            gradeRepository.save(new Grade(st1, s3, 13, "2024-2025"));
            gradeRepository.save(new Grade(st1, s1, 15, "2025-2026"));
            gradeRepository.save(new Grade(st1, s2, 17, "2025-2026"));

            gradeRepository.save(new Grade(st2, s1, 12, "2024-2025"));
            gradeRepository.save(new Grade(st2, s2, 10, "2024-2025"));
            gradeRepository.save(new Grade(st2, s3, 11, "2024-2025"));
            gradeRepository.save(new Grade(st2, s1, 14, "2025-2026"));
            gradeRepository.save(new Grade(st2, s3, 12, "2025-2026"));
            System.out.println("Data Initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR during Data Initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
