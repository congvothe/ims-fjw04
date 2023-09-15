package com.fa.ims.service;

import com.fa.ims.entities.Level;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LevelService{
    List<Level> findAll();
}
