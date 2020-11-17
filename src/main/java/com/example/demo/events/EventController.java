package com.example.demo.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



//hateoas 1.0.2 버전 이후부터는 mvc 패키지가 빠졌서 인지.. 위와 같이 static import 추가해서 해결

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController{

	private final EventRespository eventRepository;
	
	private final ModelMapper modelMapper;
	
	public EventController(EventRespository eventRepository, ModelMapper modelMapper) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping // 입력값을 제한하기 위해서 event객체를 eventDto 객체로 변경
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		System.out.println("test!!!!!!!!!!!!");
		
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		
		/*
		 * event -> eventDto로 변경함으로써 eventDto를 하나하나 event로 바꿔줘야 함 
		 * 
		 * ex)
		 * Event event = Event.builder() .name(eventDto.getName())
		 * 				   				 .description(eventDto.getDescription()) 
		 * 								 .build();
		 */
		
		// 위의 과정을 단순화하기 위해서 modelMapper 사용 maven에 의존성 등록 후 bean으로 modelmapper 등록
		Event event = modelMapper.map(eventDto, Event.class);
		Event newEvent = this.eventRepository.save(event);
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createdUri).body(event);
	}
}
