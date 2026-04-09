package org.example.controller;

import org.example.dto.BulletinData;
import org.example.service.BulletinPdfService;
import org.example.service.BulletinService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/etudiant")
public class StudentController {

    private final BulletinService bulletinService;
    private final BulletinPdfService bulletinPdfService;

    public StudentController(BulletinService bulletinService, BulletinPdfService bulletinPdfService) {
        this.bulletinService = bulletinService;
        this.bulletinPdfService = bulletinPdfService;
    }

    @GetMapping("/bulletin")
    public String bulletin(Authentication authentication,
                          @RequestParam(required = false) String academicYear,
                          Model model) {
        String studentId = authentication.getName();
        List<String> years = bulletinService.getAvailableAcademicYears(studentId);
        String selectedYear = academicYear;
        if (selectedYear == null || selectedYear.isBlank()) {
            selectedYear = years.isEmpty() ? "N/A" : years.get(0);
        }

        BulletinData bulletin = bulletinService.getBulletinByStudentIdAndYear(studentId, selectedYear);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("academicYears", years);
        model.addAttribute("selectedYear", selectedYear);
        return "student-bulletin";
    }

    @GetMapping("/bulletin/pdf")
    public ResponseEntity<byte[]> downloadPdf(Authentication authentication,
                                              @RequestParam(required = false) String academicYear) {
        String studentId = authentication.getName();
        String selectedYear = academicYear;
        if (selectedYear == null || selectedYear.isBlank()) {
            List<String> years = bulletinService.getAvailableAcademicYears(studentId);
            selectedYear = years.isEmpty() ? "N/A" : years.get(0);
        }

        BulletinData bulletin = bulletinService.getBulletinByStudentIdAndYear(studentId, selectedYear);
        byte[] pdf = bulletinPdfService.createPdf(bulletin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("bulletin-" + bulletin.studentId() + "-" + selectedYear + ".pdf")
                .build());

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
