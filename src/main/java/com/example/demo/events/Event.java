package com.example.demo.events;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ManyToAny;

import com.example.demo.accounts.Account;
import com.example.demo.accounts.AccountSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

	@Id @GeneratedValue
	private Integer id;
	private String name; 
	private String description; 
	private LocalDateTime beginEnrollmentDateTime; 
	private LocalDateTime closeEnrollmentDateTime; 
	private LocalDateTime beginEventDateTime; 
	private LocalDateTime endEventDateTime;
	private String location; // (optional) 이게 없으면 온라인 모임 
	private int basePrice; // (optional)
	private int maxPrice; // (optional) 
	private int limitOfEnrollment;
	private boolean offline;
	private boolean free;
	@Enumerated(EnumType.STRING) //ordinal 순서로 저장되기 때문에 나중에 순서가 바뀌면 문제가 생길 수 있다 그러므로 STRING 추천
	private EventStatus eventStatus = EventStatus.DRAFT;
	@ManyToOne
	@JsonSerialize(using = AccountSerializer.class)
	private Account manager;
	
	public void update() {
		// Update free
		if (this.basePrice == 0 && this.maxPrice == 0) {
			this.free = true;
		} else {
			this.free = false;
		}
		
		// Update offline
		if (this.location == null || this.location.isBlank()) {
			this.offline = false;
		} else {
			this.offline = true;
		}
	}
	
}
