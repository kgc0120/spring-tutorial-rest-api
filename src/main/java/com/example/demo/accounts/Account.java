package com.example.demo.accounts;

import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

	@Id @GeneratedValue
	private Integer id;
	
	private String email;
	
	private String password;
	
	@ElementCollection(fetch = FetchType.EAGER)
	// 여러개의 enum을 가질수 있다 
	// 기본적으로 lazy FetchType인데 이 경우 가져올 role이 적은데다가 이 accout 가져올 때마다 필요한 정보라서 EAGER로 설정
	@Enumerated(EnumType.STRING)
	private Set<AccountRole> roles;
}
