package com.example.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.cursomc.domain.enums.TipoCliente;
import com.example.cursomc.dto.ClienteNewDTO;
import com.example.cursomc.resources.exception.FieldMessage;
import com.example.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO>{

	@Override
	public void initialize(ClienteInsert constraintAnnotation) {
//		ConstraintValidator.super.initialize(constraintAnnotation);
	}
	
	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {
		List<FieldMessage> list = new ArrayList<FieldMessage>();
		//inclua os testes aqui, inserindo erros na lista
		
//		if (objDto.getTipo() == null) {
//			list.add(new FieldMessage("tipo", "Tipo não pode ser nulo"));
//		}
		if (objDto.getTipo().equals(TipoCliente.PESSOA_FISICA.getCod()) && 
				!BR.isValidCPF(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido"));
		}
		if (objDto.getTipo().equals(TipoCliente.PESSOA_JURIDICA.getCod()) && 
				!BR.isValidCNPJ(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido"));
		}
		
		// Adiciona as mensagens de erros "customizados" na lista de erros padrão do framework
		for (FieldMessage fieldMessage : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(fieldMessage.getMessage())
				.addPropertyNode(fieldMessage.getFieldName())
				.addConstraintViolation();
		}
		return list.isEmpty();
	}

}
