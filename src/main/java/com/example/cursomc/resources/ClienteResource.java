package com.example.cursomc.resources;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.cursomc.domain.Cliente;
import com.example.cursomc.domain.Cliente;
import com.example.cursomc.dto.ClienteDTO;
import com.example.cursomc.dto.ClienteNewDTO;
import com.example.cursomc.services.ClienteService;
import com.example.cursomc.services.ClienteService;

@RestController
@RequestMapping(value="/clientes")
public class ClienteResource {
	
	@Autowired
	private ClienteService service;
	
	private static final Logger logger = LoggerFactory.getLogger(FilesResource.class);
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<Cliente> find(@PathVariable Integer id) {
//		public ResponseEntity<?> find(@PathVariable Integer id) {
		
		Cliente cli = service.find(id);
		return ResponseEntity.ok().body(cli);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody ClienteNewDTO objDto){
		Cliente obj = service.fromDTO(objDto);
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Void> update(@Valid @RequestBody ClienteDTO objDto, @PathVariable Integer id){
		Cliente obj = service.fromDTO(objDto);
		obj.setId(id);
		obj = service.update(obj);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ClienteDTO>> findAll() {
//		public ResponseEntity<?> find(@PathVariable Integer id) {
		
		List<Cliente> listCat = service.findAll();
		//percorre a lista, elemento a elemento
		// função map => mapeia cada elemento da lista aplicando uma função lambda
		// função collect => devolve o resultado para uma nova lista através do argumento: Collectors.toList()
		//converte a lista para outra lista
		List<ClienteDTO> listDto = listCat.stream().map(obj -> new ClienteDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
		
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/page", method=RequestMethod.GET)
	public ResponseEntity<Page<ClienteDTO>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderBy", defaultValue="nome") String orderBy, 
			@RequestParam(value="direction", defaultValue="ASC") String direction) {
		
		Page<Cliente> listCat = service.findPage(page, linesPerPage, orderBy, direction);
		//percorre a lista, elemento a elemento
		// função map => mapeia cada elemento da lista aplicando uma função lambda
		//converte a lista para outra lista
		Page<ClienteDTO> listDto = listCat.map(obj -> new ClienteDTO(obj));
		return ResponseEntity.ok().body(listDto);
		
	}
	
	@RequestMapping(value="/picture", method=RequestMethod.POST)
	public ResponseEntity<Void> uploadProfilePicture(@RequestParam(name="file") MultipartFile multipartFile){
		String fileName = service.uploadProfilePicture(multipartFile);
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
//				.fromCurrentContextPath()
//				.path("/clientes")
//				.path("/picture")
				.path("/{fileName}").buildAndExpand(fileName).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/picture/{fileName:.+}", method=RequestMethod.GET)
	public ResponseEntity<Resource> showFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = service.loadProfilePicture(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Não foi possível determinar o tipo de arquivo.");
//        	System.out.println("Não foi possível determinar o tipo de arquivo.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
