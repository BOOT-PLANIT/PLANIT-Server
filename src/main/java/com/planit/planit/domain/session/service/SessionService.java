package com.planit.planit.domain.session.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planit.planit.domain.bootcamp.service.BootcampService;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.exception.SessionNotFoundException;
import com.planit.planit.domain.session.mapper.SessionMapper;

@Service
public class SessionService {
    private final SessionMapper sessionMapper;
    private final BootcampService bootcampService;

    public SessionService(SessionMapper sessionMapper, BootcampService bootcampService) {
        this.sessionMapper = sessionMapper;
        this.bootcampService = bootcampService;
    }

    public List<SessionDTO> getAllSessions() {
        return sessionMapper.findAll();
    }

    public List<SessionDTO> getSessionsByBootcampId(Long bootcampId) {
        // 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
        bootcampService.getBootcamp(bootcampId);
        return sessionMapper.findByBootcampId(bootcampId);
    }

    public SessionDTO getSession(Long id) {
        SessionDTO session = sessionMapper.findById(id);
        if (session == null) {
            throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
        }
        return session;
    }

    @Transactional
    public void addSession(SessionDTO session) {
        // 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
        bootcampService.getBootcamp(session.getBootcampId());
        sessionMapper.insert(session);
    }

    @Transactional
    public void updateSession(SessionDTO session) {
        SessionDTO existingSession = sessionMapper.findById(session.getId());
        if (existingSession == null) {
            throw new SessionNotFoundException("ID가 " + session.getId() + "인 세션을 찾을 수 없습니다.");
        }
        // 부트캠프 존재 여부 검증 (존재하지 않으면 BootcampNotFoundException 발생)
        bootcampService.getBootcamp(session.getBootcampId());
        sessionMapper.update(session);
    }

    @Transactional
    public void deleteSession(Long id) {
        SessionDTO existingSession = sessionMapper.findById(id);
        if (existingSession == null) {
            throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
        }
        sessionMapper.delete(id);
    }
}