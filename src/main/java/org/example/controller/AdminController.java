package org.example.controller;

import org.example.model.Grade;
import org.example.model.Professor;
import org.example.model.Student;
import org.example.model.Subject;
import org.example.repository.GradeRepository;
import org.example.repository.ProfessorRepository;
import org.example.repository.StudentRepository;
import org.example.repository.SubjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;

    public AdminController(StudentRepository studentRepository,
                           ProfessorRepository professorRepository,
                           SubjectRepository subjectRepository,
                           GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/etudiants")
    public String studentsList(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("newStudent", new Student());
        return "admin-students-list";
    }

    @GetMapping("/professeurs")
    public String professorsList(Model model) {
        model.addAttribute("professors", professorRepository.findAll());
        model.addAttribute("newProfessor", new Professor());
        return "admin-professors-list";
    }

    @GetMapping("/matieres")
    public String subjectsList(Model model) {
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("professors", professorRepository.findAll());
        model.addAttribute("newSubject", new Subject());
        return "admin-subjects-list";
    }

    @GetMapping("/notes")
    public String gradesList(@RequestParam(required = false) Long subjectId,
                             @RequestParam(required = false) String academicYear,
                             Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        List<Grade> grades = gradeRepository.findAllByOrderByAcademicYearDescSubject_NameAsc();
        if (subjectId != null) {
            grades = grades.stream()
                    .filter(g -> g.getSubject().getId().equals(subjectId))
                    .toList();
        }
        if (academicYear != null && !academicYear.isBlank()) {
            grades = grades.stream()
                    .filter(g -> academicYear.equals(g.getAcademicYear()))
                    .toList();
        }
        model.addAttribute("grades", grades);
        model.addAttribute("academicYears", gradeRepository.findDistinctAcademicYears());
        model.addAttribute("filterSubjectId", subjectId);
        model.addAttribute("filterAcademicYear", academicYear == null ? "" : academicYear);
        model.addAttribute("newGrade", new Grade());
        return "admin-grades-list";
    }

    @PostMapping("/students")
    public String addStudent(@ModelAttribute("newStudent") Student student) {
        studentRepository.save(student);
        return "redirect:/admin/etudiants";
    }

    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentRepository.findById(id).orElseThrow());
        return "admin-student-edit";
    }

    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable Long id,
                                @RequestParam String studentId,
                                @RequestParam String fullName) {
        Student existing = studentRepository.findById(id).orElseThrow();
        existing.setStudentId(studentId);
        existing.setFullName(fullName);
        studentRepository.save(existing);
        return "redirect:/admin/etudiants";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        Student s = studentRepository.findById(id).orElseThrow();
        gradeRepository.deleteAll(gradeRepository.findByStudent(s));
        studentRepository.delete(s);
        return "redirect:/admin/etudiants";
    }

    @PostMapping("/professors")
    public String addProfessor(@ModelAttribute("newProfessor") Professor professor) {
        professorRepository.save(professor);
        return "redirect:/admin/professeurs";
    }

    @GetMapping("/professors/{id}/edit")
    public String editProfessorForm(@PathVariable Long id, Model model) {
        model.addAttribute("professor", professorRepository.findById(id).orElseThrow());
        return "admin-professor-edit";
    }

    @PostMapping("/professors/{id}")
    public String updateProfessor(@PathVariable Long id,
                                  @RequestParam String fullName,
                                  @RequestParam String email) {
        Professor existing = professorRepository.findById(id).orElseThrow();
        existing.setFullName(fullName);
        existing.setEmail(email);
        professorRepository.save(existing);
        return "redirect:/admin/professeurs";
    }

    @PostMapping("/professors/{id}/delete")
    public String deleteProfessor(@PathVariable Long id) {
        Professor p = professorRepository.findById(id).orElseThrow();
        for (Subject s : subjectRepository.findByProfessor(p)) {
            gradeRepository.deleteAll(gradeRepository.findBySubject(s));
            subjectRepository.delete(s);
        }
        professorRepository.delete(p);
        return "redirect:/admin/professeurs";
    }

    @PostMapping("/subjects")
    public String addSubject(@ModelAttribute("newSubject") Subject subject) {
        subjectRepository.save(subject);
        return "redirect:/admin/matieres";
    }

    @GetMapping("/subjects/{id}/edit")
    public String editSubjectForm(@PathVariable Long id, Model model) {
        model.addAttribute("subject", subjectRepository.findById(id).orElseThrow());
        model.addAttribute("professors", professorRepository.findAll());
        return "admin-subject-edit";
    }

    @PostMapping("/subjects/{id}")
    public String updateSubject(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam double coefficient,
                                @RequestParam Long professorId) {
        Subject existing = subjectRepository.findById(id).orElseThrow();
        Professor prof = professorRepository.findById(professorId).orElseThrow();
        existing.setName(name);
        existing.setCoefficient(coefficient);
        existing.setProfessor(prof);
        subjectRepository.save(existing);
        return "redirect:/admin/matieres";
    }

    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(@PathVariable Long id) {
        Subject s = subjectRepository.findById(id).orElseThrow();
        gradeRepository.deleteAll(gradeRepository.findBySubject(s));
        subjectRepository.delete(s);
        return "redirect:/admin/matieres";
    }

    @PostMapping("/grades")
    public String addGrade(@ModelAttribute("newGrade") Grade grade) {
        gradeRepository.save(grade);
        return "redirect:/admin/notes";
    }

    @GetMapping("/grades/{id}/edit")
    public String editGradeForm(@PathVariable Long id, Model model) {
        model.addAttribute("grade", gradeRepository.findById(id).orElseThrow());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "admin-grade-edit";
    }

    @PostMapping("/grades/{id}")
    public String updateGrade(@PathVariable Long id,
                              @RequestParam Long studentId,
                              @RequestParam Long subjectId,
                              @RequestParam double value,
                              @RequestParam String academicYear) {
        Grade existing = gradeRepository.findById(id).orElseThrow();
        Student st = studentRepository.findById(studentId).orElseThrow();
        Subject sub = subjectRepository.findById(subjectId).orElseThrow();
        existing.setStudent(st);
        existing.setSubject(sub);
        existing.setValue(value);
        existing.setAcademicYear(academicYear);
        gradeRepository.save(existing);
        return "redirect:/admin/notes";
    }

    @PostMapping("/grades/{id}/delete")
    public String deleteGrade(@PathVariable Long id) {
        gradeRepository.deleteById(id);
        return "redirect:/admin/notes";
    }
}
