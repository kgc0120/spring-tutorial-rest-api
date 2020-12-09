package com.example.demo.events;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.accounts.Account;
import com.example.demo.accounts.AccountAdapter;
import com.example.demo.accounts.CurrentUser;
import com.example.demo.common.ErrorsResource;


//hateoas 1.0.2 버전 이후부터는 mvc 패키지가 빠졌서 인지.. 위와 같이 static import 추가해서 해결

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController{

	private final EventRespository eventRepository;
	
	private final ModelMapper modelMapper;
	
	private final EventValidator eventValidator;
	
	public EventController(EventRespository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}
	
	@PostMapping // 입력값을 제한하기 위해서 event객체를 eventDto 객체로 변경
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, 
									Errors errors,
									@CurrentUser Account currentUser) {
		if(errors.hasErrors()) {
			return badRequest(errors);
		}
		
		eventValidator.validate(eventDto, errors);
		if(errors.hasErrors()) {
			return badRequest(errors);
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
		event.update();
		event.setManager(currentUser);
		Event newEvent = this.eventRepository.save(event);
		
		// Link 추가
		ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
		URI createdUri = selfLinkBuilder.toUri();
		EventResource eventResource = new EventResource(event);
		eventResource.add(linkTo(EventController.class).withRel("query-events"));
		// eventResource.add(selfLinkBuilder.withSelfRel());
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		return ResponseEntity.created(createdUri).body(eventResource);
	}
	
	@GetMapping
	public ResponseEntity queryEvents(Pageable pageable, 
										PagedResourcesAssembler<Event> assembler,
										@CurrentUser /* 메타 애노테이션 만들어서 사용 */ Account account) {
		
		Page<Event> page =  this.eventRepository.findAll(pageable);
		var pagedResources =  assembler.toResource(page, e -> new EventResource(e));
		if(account != null) {
			pagedResources.add(linkTo(EventController.class).withRel("create-event"));
		}
		return ResponseEntity.ok(pagedResources);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id,
									@CurrentUser Account currentUser) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if (optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		if(event.getManager().equals(currentUser)) {
			eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
		}
		
		return ResponseEntity.ok(eventResource);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id, 
									  @RequestBody @Valid EventDto eventDto,
									  Errors errors,
									  @CurrentUser Account currentUser) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		if(optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		if (errors.hasErrors()) {
			return badRequest(errors);
		}
		
		this.eventValidator.validate(eventDto, errors);
		if (errors.hasErrors()) {
			return badRequest(errors);
		}
		
		Event existingEvent = optionalEvent.get();
		if(!existingEvent.getManager().equals(currentUser)) {
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		
		this.modelMapper.map(eventDto, existingEvent);
		Event saveEvent = this.eventRepository.save(existingEvent);
		EventResource eventResource = new EventResource(saveEvent);
		
		return ResponseEntity.ok(eventResource);
	}
	
	
	private ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
