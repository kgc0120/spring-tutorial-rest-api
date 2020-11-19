package com.example.demo.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class EventTest {

	@Test
	public void builder() {
		Event event = Event.builder()
				.name("Inflean Spring Rest Api")
				.description("Rest Api development with spring")
				.build();
		assertThat(event).isNotNull();
	}
	
	@Test
	public void javaBean() {
		String name = "Event";
		String description = "Spring";
		
		Event event = new Event();
		event.setName(name);
		event.setDescription(description);
		
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(description);
		
	}
	
	@Test
	public void testFree() {
		// Given 이런 상태에서
		Event event = Event.builder()
				.basePrice(0)
				.maxPrice(0)
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		
		// Then 이렇게 된다
		assertThat(event.isFree()).isTrue();
		
		// Given 이런 상태에서
		event = Event.builder()
				.basePrice(100)
				.maxPrice(0)
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		
		// Then 이렇게 된다
		assertThat(event.isFree()).isFalse();
		
		// Given 이런 상태에서
		event = Event.builder()
				.basePrice(0)
				.maxPrice(100)
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		
		// Then 이렇게 된다
		assertThat(event.isFree()).isFalse();
		
	}
	
	@Test
	public void testOffline() {
		// Given 이런 상태에서
		Event event = Event.builder()
				.location("낙성대 5번 출구 앞 빌딩")
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		
		// Then 이렇게 된다
		assertThat(event.isOffline()).isTrue();
		
		// Given 이런 상태에서
		event = Event.builder()
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		// Then 이렇게 된다
		assertThat(event.isOffline()).isFalse();
	}
}
