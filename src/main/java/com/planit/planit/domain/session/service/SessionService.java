package com.planit.planit.domain.session.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.planit.planit.domain.session.dto.SessionDTO;
import com.planit.planit.domain.session.exception.SessionNotFoundException;
import com.planit.planit.domain.session.mapper.SessionMapper;

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
    SessionDTO session = sessionMapper.findById(id);
    if (session == null) {
      throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
    }
    return session;
  }

  public void addSession(SessionDTO session) {
    sessionMapper.insert(session);
  }

  public void updateSession(SessionDTO session) {
    SessionDTO existingSession = sessionMapper.findById(session.getId());
    if (existingSession == null) {
      throw new SessionNotFoundException("ID가 " + session.getId() + "인 세션을 찾을 수 없습니다.");
    }
    sessionMapper.update(session);
  }

  public void deleteSession(Long id) {
    SessionDTO existingSession = sessionMapper.findById(id);
    if (existingSession == null) {
      throw new SessionNotFoundException("ID가 " + id + "인 세션을 찾을 수 없습니다.");
    }
    sessionMapper.delete(id);
  }
}
