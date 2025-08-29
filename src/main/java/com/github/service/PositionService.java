package com.github.service;

import com.github.dto.PositionDto;
import com.github.repository.PositionJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionJdbcRepository positionRepo;

    @Transactional(readOnly = true)
    public List<PositionDto> getAllPositions() {
        return positionRepo.findAll();
    }

}
