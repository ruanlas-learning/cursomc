package com.example.cursomc.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.cursomc.domain.Categoria;
import com.example.cursomc.dto.CategoriaDTO;
import com.example.cursomc.services.CategoriaService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value="/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaService service;
	
	@ApiOperation(value="Busca por id")
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<Categoria> find(@PathVariable Integer id) {
//		public ResponseEntity<?> find(@PathVariable Integer id) {
		
		Categoria cat = service.find(id);
		return ResponseEntity.ok().body(cat);
		
//		Categoria cat1 = new Categoria(1, "Informática");
//		Categoria cat2 = new Categoria(2, "Escritório");
//		
//		List<Categoria> lista = new ArrayList<Categoria>();
//		lista.add(cat1);
//		lista.add(cat2);
//		
////		return "REST está funcionando!";
//		return lista;
	}
	
	@ApiOperation(value="Insere categoria")
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody CategoriaDTO objDto){
		Categoria obj = service.fromDTO(objDto);
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@ApiOperation(value="Atualiza categoria")
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Void> update(@Valid @RequestBody CategoriaDTO objDto, @PathVariable Integer id){
		Categoria obj = service.fromDTO(objDto);
		obj.setId(id);
		obj = service.update(obj);
		return ResponseEntity.noContent().build();
	}
	
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Não é possível excluir uma categoria que possui produtos"),
			@ApiResponse(code = 404, message = "Código inexistente") })
	@ApiOperation(value="Remove categoria")
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@ApiOperation(value="Retorna todas categorias")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<CategoriaDTO>> findAll() {
//		public ResponseEntity<?> find(@PathVariable Integer id) {
		
		List<Categoria> listCat = service.findAll();
		//percorre a lista, elemento a elemento
		// função map => mapeia cada elemento da lista aplicando uma função lambda
		// função collect => devolve o resultado para uma nova lista através do argumento: Collectors.toList()
		//converte a lista para outra lista
		List<CategoriaDTO> listDto = listCat.stream().map(obj -> new CategoriaDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
		
	}
	
	@ApiOperation(value="Retorna todas categorias com paginação")
	@RequestMapping(value="/page", method=RequestMethod.GET)
	public ResponseEntity<Page<CategoriaDTO>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderBy", defaultValue="nome") String orderBy, 
			@RequestParam(value="direction", defaultValue="ASC") String direction) {
		
		Page<Categoria> listCat = service.findPage(page, linesPerPage, orderBy, direction);
		//percorre a lista, elemento a elemento
		// função map => mapeia cada elemento da lista aplicando uma função lambda
		//converte a lista para outra lista
		Page<CategoriaDTO> listDto = listCat.map(obj -> new CategoriaDTO(obj));
		return ResponseEntity.ok().body(listDto);
		
	}
}
