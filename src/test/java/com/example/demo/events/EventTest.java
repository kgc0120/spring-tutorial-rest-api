package com.example.demo.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
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
	@Parameters// (method = "parametersForTestFree") method 이름과 test 메소드 이름 앞에 parameters 제외하고 같으면 생략 가능
	public void testFree(int basePrice, int maxPrice, boolean isFree) {
		// Given 이런 상태에서
		Event event = Event.builder()
				.basePrice(basePrice)
				.maxPrice(maxPrice)
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		
		// Then 이렇게 된다
		assertThat(event.isFree()).isEqualTo(isFree);
	}
	
	private Object[] parametersForTestFree() {
		return new Object[] {
			new Object[] {0, 0, true},
			new Object[] {100, 0, false},
			new Object[] {0, 100, false},
			new Object[] {100, 200, false}
		};
	}
	
	@Test
	@Parameters (method = "parametersForTestOffline")
	public void testOffline(String location, boolean isOffline) {
		// Given 이런 상태에서
		Event event = Event.builder()
				.location(location)
				.build();
		
		// When 이런일이 벌어지면
		event.update();
		
		
		// Then 이렇게 된다
		assertThat(event.isOffline()).isEqualTo(isOffline);
	}
	
	private Object[] parametersForTestOffline() {
		return new Object[] {
			new Object[] {"강남",  true},
			new Object[] {null, false},
			new Object[] {"    ", false},
		};
	}
}
