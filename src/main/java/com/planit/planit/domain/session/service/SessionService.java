package com.planit.planit.domain.session.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.planit.planit.domain.session.mapper.SessionMapper;
import com.planit.planit.domain.session.dto.SessionDTO;

@Service
public class SessionService {
    private final SessionMapper sessionMapper;

    public SessionService(SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }

    public List<SessionDTO> getAllSessions() {
        return sessionMapper.findAll();
    }

    public List<SessionDTO> getSessionsByBootcampId(Long bootcampId) {
        return sessionMapper.findByBootcampId(bootcampId);
    }

    public SessionDTO getSession(Long id) {
        return sessionMapper.findById(id);
    }

    public void addSession(SessionDTO session) {
        sessionMapper.insert(session);
    }

    public void updateSession(SessionDTO session) {
        sessionMapper.update(session);
    }

    public void deleteSession(Long id) {
        sessionMapper.delete(id);
    }
}
