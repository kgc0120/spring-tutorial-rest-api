package com.example.demo.configs;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.accounts.Account;
import com.example.demo.accounts.AccountRepository;
import com.example.demo.accounts.AccountRole;
import com.example.demo.accounts.AccountService;
import com.example.demo.common.AppProperties;

@Configuration
public class AppConfig {

		//modelMapper bean으로 등록
		@Bean
		public ModelMapper modelMapper() {
			return new ModelMapper();
		}
		
		@Bean
		public PasswordEncoder passwordEncoder() {
			return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		}
		
		@Bean // 같은 유저정보가 두번 입력 되어서 테스트 실패에 따라서 주석처리
		public ApplicationRunner applicationRunner() {
			return new ApplicationRunner() {
				
				@Autowired
				AccountService accountService;
				
				@Autowired
				AppProperties appProperties;
				
				@Override
				public void run(ApplicationArguments args) throws Exception {
					Account admin =  Account.builder()
											.email(appProperties.getAdminUsername())
											.password(appProperties.getAdminPassword())
											.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
											.build();
					accountService.saveAccount(admin);
					
					Account user =  Account.builder()
							.email(appProperties.getUserUsername())
							.password(appProperties.getUserPassword())
							.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
							.build();
					accountService.saveAccount(user);
				}
			};
		}
}
