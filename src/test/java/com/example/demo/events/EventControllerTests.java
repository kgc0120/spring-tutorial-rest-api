package com.example.demo.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.demo.common.BaseControllerTest;

public class EventControllerTests extends BaseControllerTest{

	@Autowired
	EventRespository eventRepository;
	
	/*
	 * @MockBean EventRespository eventRepository;
	 */
	
//	no runnable methods관련 오류 메시지
//	시도 1. @Test 애노테이션이 안 붙어 있으면 나는 오류메시지 -> 모두 붙어있었음 (해결 x)
//	시도 2. public 접근 제어자가 없으면 오류메시지 사라진다 -> 효과 없었음 (해결 x)
//	시도 3. import가 잘못되어있었다. junit.jupiter.api.Test -> junit.Test로 변경(해결 o)
	@Test
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 10, 11, 15))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 17, 11, 15))
				.beginEventDateTime(LocalDateTime.of(2020, 11, 10, 11, 15))
				.endEventDateTime(LocalDateTime.of(2020, 11, 20, 11, 15))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("낙성대 5번 출구")
				.build(); 
//		Mockito.when(eventRepository.save(event)).thenReturn(event);
		
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON_UTF8_VALUE)
				.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(jsonPath("free").value(false))
				.andExpect(jsonPath("offline").value(true))
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.query-events").exists())
				.andExpect(jsonPath("_links.update-event").exists())
				.andDo(document("create-event",
						links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event")
                        ),
						requestHeaders(
								headerWithName(HttpHeaders.ACCEPT).description("accept header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
						),
						requestFields(
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("descript of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment")
						),
						responseHeaders(
								headerWithName(HttpHeaders.LOCATION).description("Location header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
						),
						// relaxedResponseFields
						// 장점 : 문서 일부분만 테스트 가능하다.
						// 단점 : 정확한 문서를 생성하지 못한다.
						responseFields(
								fieldWithPath("id").description("identifier of new event"),
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("descript of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event")
						)
				))
		;
		
//		java.lang.SecurityException: class "org.hamcrest.Matchers"'s signer information does not match signer 
//		information of other classes in the same package
//		
//		위와 같이 Matchers 관련 오류 메시지 해결 방법
//		이클립스안에 junt관련 jar가 내장되어있으니 Java Build Path에서 Junit 라이브러리를 제거하는 것이다.
//		메이븐 라이브러리에도 junit과 hamcrest가 들어있는데 따로 Junit 라이브러리도 추가하니 중복으로 기입되면서 
//		pom.xml에서 jar 순서가 바뀐 것과 같은 효과가 발생한 것이다.
//		
//		참조 - https://net4all.tistory.com/133
	}
	
	@Test
	public void createEvent_Bad_Request() throws Exception {
//		   spring.jackson.deserialization.fail-on-unknown-properties=true
//		   application.properties에 위와 같이 설정 하면
//		   nuknown properties를 넘기면 에러 발생하기 때문에 Bad_Request로 처리
		Event event = Event.builder()
				.id(100)
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 10, 11, 15))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("낙성대 5번 출구")
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.PUBLISHED)
				.build(); 
		
		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
		
	}
	
	@Test
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 20, 11, 15))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 17, 11, 15))
				.beginEventDateTime(LocalDateTime.of(2020, 11, 29, 11, 15))
				.endEventDateTime(LocalDateTime.of(2020, 11, 20, 11, 15))
				.build();
		
		this.mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void createEvent_Bad_Request_Wrong_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 20, 11, 15))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 17, 11, 15))
				.beginEventDateTime(LocalDateTime.of(2020, 11, 29, 11, 15))
				.endEventDateTime(LocalDateTime.of(2020, 11, 20, 11, 15))
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("낙성대 5번 출구")
				.build();
		
		this.mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(eventDto)))
					.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("content[0].objectName").exists())
				.andExpect(jsonPath("content[0].defaultMessage").exists())
				.andExpect(jsonPath("content[0].code").exists())
				.andExpect(jsonPath("_links.index").exists())
				;
	}
	
	
	@Test
	public void queryEvents() throws Exception {
		// Given
		IntStream.range(0, 30).forEach(i -> {
			this.generateEvent(i);
		});
		
		// When & Then
		this.mockMvc.perform(get("/api/events")
					.param("page", "1")
					.param("size", "10")
					.param("sort", "name,DESC")
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
				.andExpect(jsonPath("_links.self").exists())
				// .andExpect(jsonPath("_links.profile").exists())
				.andDo(document("query-events"))
		;
	}
	
	@Test
	public void getEvent() throws Exception {
		//Given
		Event event = this.generateEvent(100);
		
		//When & Then
		this.mockMvc.perform(get("/api/events/{id}", event.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").exists())
			.andExpect(jsonPath("id").exists())
			.andExpect(jsonPath("_links.self").exists())
			// .andExpect(jsonPath("_links.profile").exists())
			;
	}
	
	@Test
	public void getEvent404() throws Exception {
		
		//When & Then
		this.mockMvc.perform(get("/api/events/11883"))
			.andExpect(status().isNotFound())
			
		;
	}
	
	@Test
	public void updateEvent() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String eventName = "Updated Event";
		eventDto.setName(eventName);
		
		// When & Then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
					.contentType(MediaType.APPLICATION_JSON_UTF8 )
					.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(eventName))
			.andExpect(jsonPath("_links.self").exists())
			;
		
	}
	
	@Test
	public void updateEvent400_Empty() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = new EventDto();
		
		// When & Then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isBadRequest())
			;
		
	}
	
	@Test
	public void updateEvent400_Wrong() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		// When & Then
		this.mockMvc.perform(put("/api/events/{id}", event.getId())
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isBadRequest())
			;
		
	}
	
	@Test
	public void updateEvent404() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		
		// When & Then
		this.mockMvc.perform(put("/api/events/1231233")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isNotFound())
			;
		
	}
	

	private Event generateEvent(int index) {
		// TODO Auto-generated method stub
		Event event = Event.builder()
				.name("event" + index)
				.description("test event")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 10, 11, 15))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 17, 11, 15))
				.beginEventDateTime(LocalDateTime.of(2020, 11, 10, 11, 15))
				.endEventDateTime(LocalDateTime.of(2020, 11, 20, 11, 15))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("낙성대 5번 출구")
				.free(false)
				.offline(true)
				.eventStatus(EventStatus.DRAFT)
				.build();
		
		return this.eventRepository.save(event);
	}
	
	
	
}
