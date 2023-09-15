package com.fa.ims.service.impl;

import com.fa.ims.entities.Status;
import com.fa.ims.exception.ResourceNotFoundException;
import com.fa.ims.repository.StatusRepository;
import com.fa.ims.service.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;
    @Override
    public Map<Long, String> getNameStatusWithStage(String stage) {
        List<Status> statusList = statusRepository.findAllByStage(stage);
        Map<Long, String> statusMap = new LinkedHashMap<>();
        statusList.forEach(status -> statusMap.put(status.getId(), status.getName()));
        return statusMap;
    }

    @Override
    public Map<Long, String> getAllNameStatus() {
        List<Status> statusList = statusRepository.findAll();
        Map<Long, String> statusMap = new LinkedHashMap<>();
        statusList.forEach(status -> statusMap.put(status.getId(), status.getName()));
        return statusMap;
    }

    @Override
    public Status findById(Long id) {
        return statusRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
