package com.fa.ims.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public interface FileStorageService {
    String saveFile(MultipartFile file, String emailFolder) throws IOException;

    Resource loadFileAsResource(String relativePath) throws FileNotFoundException, MalformedURLException;
}
