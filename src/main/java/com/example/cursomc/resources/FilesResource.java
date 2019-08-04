package com.example.cursomc.resources;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.cursomc.services.FileStorageService;

@RestController
@RequestMapping(value="/arquivos")
public class FilesResource {
	
	private static final Logger logger = LoggerFactory.getLogger(FilesResource.class);
	
	@Autowired
	private FileStorageService fileStorageService;

	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> uploadFile(@RequestParam(name="file") MultipartFile file, @Nullable @RequestParam(name="path") String path){

		String fileName;
		URI uri;
		if (path == null) {
			fileName = fileStorageService.storeFile(file);
			uri = ServletUriComponentsBuilder.fromCurrentRequest()
					.path("/{fileName}")
					.buildAndExpand(fileName)
					.toUri();
		}else {
			fileName = fileStorageService.storeFile(file, path);
			uri = ServletUriComponentsBuilder.fromCurrentRequest()
					.queryParam("path", path)
					.queryParam("fileName", fileName)
					.build()
					.toUri();
		}

//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/otherPathUrl/")
//                .path(fileName)
//                .toUriString();
		
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/{path:.+}", method=RequestMethod.POST)
	public ResponseEntity<Void> uploadFilePath(@RequestParam(name="file") MultipartFile file, @PathVariable String path){

		String fileName;
		if (path == null) {
			fileName = fileStorageService.storeFile(file);
		}else {
			fileName = fileStorageService.storeFile(file, path);
		}

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{fileName}").buildAndExpand(fileName).toUri();

		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/{fileName:.+}", method=RequestMethod.GET)
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//        	System.out.println("Não foi possível determinar o tipo de arquivo.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
	
	@RequestMapping(value="/{path:.+}/{fileName:.+}", method=RequestMethod.GET)
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @PathVariable String path, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName, path);
		
		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//			System.out.println("Não foi possível determinar o tipo de arquivo.");
		}
		
		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Resource> downloadFilePath(@RequestParam String fileName, @RequestParam String path, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName, path);
		
		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//			System.out.println("Não foi possível determinar o tipo de arquivo.");
		}
		
		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	

	@RequestMapping(value="/show/{fileName:.+}", method=RequestMethod.GET)
	public ResponseEntity<Resource> showFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//        	System.out.println("Não foi possível determinar o tipo de arquivo.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
	

	@RequestMapping(value="/show/{path:.+}/{fileName:.+}", method=RequestMethod.GET)
	public ResponseEntity<Resource> showFile(@PathVariable String fileName, @PathVariable String path, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName, path);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//        	System.out.println("Não foi possível determinar o tipo de arquivo.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
	
	@RequestMapping(value="/show", method=RequestMethod.GET)
	public ResponseEntity<Resource> showFilePath(@RequestParam String fileName, @RequestParam String path, HttpServletRequest request) {
        // Load file as Resource

        Resource resource = fileStorageService.loadFileAsResource(fileName, path);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//        	System.out.println("Não foi possível determinar o tipo de arquivo.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
