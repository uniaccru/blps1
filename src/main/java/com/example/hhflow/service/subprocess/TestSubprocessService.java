package com.example.hhflow.service.subprocess;

import org.springframework.stereotype.Service;

@Service
public class TestSubprocessService {

    public boolean passTest(Long candidateId, Long vacancyId, Boolean forcedResult) {
        if (forcedResult != null) {
            return forcedResult;
        }
        return true;
    }
}
