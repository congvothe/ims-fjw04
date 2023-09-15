package com.fa.ims.service.impl;

import com.fa.ims.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileLocalStorageService implements FileStorageService {
    @Value("${app.file.location}")
    String fileLocation;

    @Override
    public String saveFile(MultipartFile file, String emailFolder) throws IOException {
        Objects.requireNonNull(file.getOriginalFilename());
        Path folderUserPath = Paths.get(fileLocation).resolve(emailFolder);
        Files.createDirectories(folderUserPath);
        Path relativePathFile = Paths.get(emailFolder).resolve(file.getOriginalFilename());
        file.transferTo(folderUserPath.resolve(file.getOriginalFilename()));
        return relativePathFile.toString();
    }

    @Override
    public Resource loadFileAsResource(String relativePath) throws FileNotFoundException, MalformedURLException {
        Path absolutePath = Paths.get(fileLocation).resolve(relativePath);
        Resource resource = new UrlResource(absolutePath.toUri());
        if (resource.exists()) {
            return resource;
        }
        throw new FileNotFoundException("Can not find with relative path: " + relativePath);
    }
}
