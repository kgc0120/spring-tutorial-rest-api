package com.example.demo.common;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

//spring boot 저절로 바인딩 받아준다.
@Component
@ConfigurationProperties("myApp")
@Getter @Setter
public class AppProperties {

	@NotEmpty
	private String adminUsername;
	
	@NotEmpty
	private String adminPassword;
	
	@NotEmpty
	private String userUsername;
	
	@NotEmpty
	private String userPassword;
	
	@NotEmpty
	private String clientId;
	
	@NotEmpty
	private String clientSecret;
	
}
