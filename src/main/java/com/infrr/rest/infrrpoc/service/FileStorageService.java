package com.infrr.rest.infrrpoc.service;

import com.infrr.rest.infrrpoc.config.FileStorageProperties;
import com.infrr.rest.infrrpoc.exception.FileStorageException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Log4j2
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService (FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.info("error");
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile (MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded Successfully ...."+fileName);
            return fileName;
        } catch (IOException ex) {
            log.error("File unUploaded getting exception ...."+fileName);
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }
    }

    public Resource loadFileAsResource (String fileName) throws Exception {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                log.info("File founded Successfully ...."+fileName);
                return resource;
            } else {
                log.error("File not founded Successfully ...."+fileName);
                throw new Exception("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("File not founded Successfully ...."+fileName);
            throw new Exception("File not found " + fileName);
        }
    }

    public boolean deleteFromServer(String fileName) throws Exception {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                final boolean delete = resource.getFile().delete();
                if(delete)
                log.info("File deleted Successfully ...."+fileName);
                return delete;
            } else {
                log.error("File not deleted Successfully ...."+fileName);
                throw new Exception("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("File not founded .."+fileName);
            throw new Exception("File not found " + fileName);
        }
    }
}
