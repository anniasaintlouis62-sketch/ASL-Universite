package org.example.dto;

import java.util.List;

public record BulletinData(
        String studentId,
        String studentName,
        String academicYear,
        List<BulletinLine> lines,
        double average,
        String mention
) {
}
