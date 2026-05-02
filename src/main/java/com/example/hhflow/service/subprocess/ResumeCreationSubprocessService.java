package com.example.hhflow.service.subprocess;

import com.example.hhflow.model.Resume;
import com.example.hhflow.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeCreationSubprocessService {

    private final ResumeService resumeService;

    public Optional<Resume> createResume(Long applicantId, String fullName, String summary) {
        if (isBlank(fullName) || isBlank(summary)) {
            return Optional.empty();
        }

        return Optional.of(resumeService.createForApplicant(applicantId, fullName.trim(), summary.trim()));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
