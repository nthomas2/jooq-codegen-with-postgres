package com.nthomas.jooqcodegenwithpostgres.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nthomas.jooqcodegenwithpostgres.model.CreateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.LoginRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UserProfile;
import com.nthomas.jooqcodegenwithpostgres.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public UserProfile createUser(@RequestBody CreateUserRequest createUserRequest) {
		return userService.createUser(createUserRequest);
	}

	@PostMapping(path = "/login")
	public UserProfile login(@RequestBody LoginRequest loginRequest) {
		return userService.verify(loginRequest.getUsername(), loginRequest.getPassword());
	}
}
