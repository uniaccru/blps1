package com.example.hhflow.service.subprocess;

import com.example.hhflow.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationSubprocessService {

    private final ApplicantService applicantService;

    public boolean registerIfMissing(Long applicantId, Boolean forcedResult) {
        if (applicantService.existsById(applicantId)) {
            return true;
        }
        if (Boolean.FALSE.equals(forcedResult)) {
            return false;
        }
        applicantService.create(applicantId);
        return true;
    }
}
