package com.example.demo.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


// BeanSerializer 사용
public class EventResource extends Resource<Event>{

	//명시적으로 @JsonUnwrapped 해주지 않아도 된다.
	public EventResource(Event event, Link... links) {
		super(event, links);
		add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}

}
