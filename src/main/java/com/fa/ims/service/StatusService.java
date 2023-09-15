package com.fa.ims.service;

import com.fa.ims.entities.Status;

import java.util.Map;

public interface StatusService {
    Map<Long, String> getNameStatusWithStage(String stage);

    Map<Long, String> getAllNameStatus();

    Status findById(Long id);
}
