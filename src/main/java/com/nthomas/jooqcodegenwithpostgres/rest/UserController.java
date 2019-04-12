package com.nthomas.jooqcodegenwithpostgres.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nthomas.jooqcodegenwithpostgres.model.CreateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.LoginRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UpdateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UserProfile;
import com.nthomas.jooqcodegenwithpostgres.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "User", tags = "User")
@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@ApiOperation(value = "Create a new user")
	@PostMapping
	public UserProfile createUser(@RequestBody CreateUserRequest createUserRequest) {
		return userService.createUser(createUserRequest);
	}

	@ApiOperation(value = "Update an existing user")
	@PutMapping
	public UserProfile updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
		return userService.updateUser(updateUserRequest);
	}

	@ApiOperation(value = "User login")
	@PostMapping(path = "/login")
	public UserProfile login(@RequestBody LoginRequest loginRequest) {
		return userService.verify(loginRequest.getUsername(), loginRequest.getPassword());
	}
}
