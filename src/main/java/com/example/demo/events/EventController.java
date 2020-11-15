package com.example.demo.events;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;

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
	
	public EventController(EventRespository eventRepository) {
		this.eventRepository = eventRepository;
	}
	
	@PostMapping
	public ResponseEntity createEvent(@RequestBody  Event event) {
		Event newEvent = this.eventRepository.save(event);
		URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
		return ResponseEntity.created(createdUri).body(event);
	}
}
