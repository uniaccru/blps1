package com.example.hhflow.service.subprocess;

import org.springframework.stereotype.Service;

@Service
public class AuthSubprocessService {

    public boolean isAuthorized(Boolean forcedResult) {
        if (forcedResult != null) {
            return forcedResult;
        }
        return true;
    }
}
