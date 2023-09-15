package com.fa.ims.service;

import java.util.List;
import java.util.Map;

public interface JobLevelService {
    Map<Long, String> mapLevelNames(List<Long> jobIds);
}
