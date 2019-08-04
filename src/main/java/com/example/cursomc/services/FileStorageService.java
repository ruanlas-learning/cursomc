package com.example.cursomc.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.cursomc.property.FileStorageProperties;
import com.example.cursomc.services.exceptions.FileStorageException;
import com.example.cursomc.services.exceptions.MyFileNotFoundException;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;
	
	private final FileStorageProperties fileStorageProperties;
	
	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		
		this.fileStorageProperties = fileStorageProperties;
		fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
				.toAbsolutePath()
				.normalize();
		
		try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Não foi possível criar o diretório onde os arquivos serão armazenados.", ex);
//        	System.out.println("Não foi possível criar o diretório onde os arquivos serão armazenados");
        }
	}
	
	public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Desculpe! O nome do arquivo contém o path " + fileName + " inválido");
//                System.out.println("Desculpe! O nome do arquivo contém o path " + fileName + " inválido");
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Não foi possível armazenar o arquivo " + fileName + ". Por favor, tente novamente!", ex);
//            System.out.println("Não foi possível armazenar o arquivo " + fileName + ". Por favor, tente novamente!");
        }
    }

	public String storeFile(MultipartFile file, String path) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Desculpe! O nome do arquivo contém o path " + fileName + " inválido");
//                System.out.println("Desculpe! O nome do arquivo contém o path " + fileName + " inválido");
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(path).resolve(fileName);
            Files.createDirectories(targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Não foi possível armazenar o arquivo " + fileName + ". Por favor, tente novamente!", ex);
//            System.out.println("Não foi possível armazenar o arquivo " + fileName + ". Por favor, tente novamente!");
        }
    }
	
	public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("Arquivo não encontrado " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("Arquivo não encontrado " + fileName, ex);
        }
    }

	public Resource loadFileAsResource(String fileName, String path) {
        try {
            Path filePath = this.fileStorageLocation.resolve(path).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("Arquivo não encontrado " + path + "/" + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("Arquivo não encontrado " + path + "/" + fileName);
        }
    }
}
