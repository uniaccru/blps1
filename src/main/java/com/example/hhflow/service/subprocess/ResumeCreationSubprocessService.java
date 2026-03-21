package com.example.hhflow.service.subprocess;

import com.example.hhflow.domain.Resume;
import com.example.hhflow.dto.CreateResumeRequest;
import com.example.hhflow.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeCreationSubprocessService {

    private final ResumeService resumeService;

    public Optional<Resume> createResume(Long candidateId, String fullName, String summary, Boolean forcedResult) {
        if (Boolean.FALSE.equals(forcedResult)) {
            return Optional.empty();
        }
        if (isBlank(fullName) || isBlank(summary)) {
            return Optional.empty();
        }

        CreateResumeRequest request = new CreateResumeRequest();
        request.setCandidateId(candidateId);
        request.setFullName(fullName.trim());
        request.setSummary(summary.trim());

        return Optional.of(resumeService.create(request));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
