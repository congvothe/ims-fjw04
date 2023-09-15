package com.fa.ims.service;

import java.util.List;
import java.util.Map;

public interface JobSkillService {
    Map<Long, String> mapSkillNames(List<Long> jobIds);
}
