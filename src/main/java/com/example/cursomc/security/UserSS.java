package com.example.cursomc.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.cursomc.domain.enums.Perfil;

public class UserSS implements UserDetails {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String email;
	private String senha;
	private Collection<? extends GrantedAuthority> authorities;

	public UserSS() {
	}
	
	public UserSS(Integer id, String email, String senha, Set<Perfil> perfils) {
		super();
		this.id = id;
		this.email = email;
		this.senha = senha;
		this.authorities = perfils.stream().map(
				x -> new SimpleGrantedAuthority(x.getDescricao())
			).collect(Collectors.toList());
	}



	public Integer getId() {
		return id;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// A conta não está espirada?
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// A conta não está bloqueada?
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// As credenciais não estão expiradas?
		return true;
	}

	@Override
	public boolean isEnabled() {
		// usário está habilitado?
		return true;
	}

	public boolean hasRole(Perfil perfil) {
		return getAuthorities()
				.contains(new SimpleGrantedAuthority(perfil.getDescricao()));
	}

}
