package com.nthomas.jooqcodegenwithpostgres.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.nthomas.jooqcodegenwithpostgres.model.CreateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.LoginRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UpdateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UserProfile;
import com.nthomas.jooqcodegenwithpostgres.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	@Mock
	private UserService userService;
	@Mock
	private UserProfile userProfile;

	private UserController userController;

	@Before
	public void init() {
		userController = new UserController(userService);
	}

	@Test
	public void testCreateUser() {
		CreateUserRequest createUserRequest = new CreateUserRequest();

		when(userService.createUser(createUserRequest)).thenReturn(userProfile);
		UserProfile result = userController.createUser(createUserRequest);

		verify(userService).createUser(createUserRequest);

		assertEquals(userProfile, result);
	}

	@Test
	public void testUpdateUser() {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest();

		when(userService.updateUser(updateUserRequest)).thenReturn(userProfile);
		UserProfile result = userController.updateUser(updateUserRequest);
		verify(userService).updateUser(updateUserRequest);

		assertEquals(userProfile, result);
	}

	@Test
	public void testLogin() {
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("TheUsername");
		loginRequest.setPassword("ThePassword");
		when(userService.verify(loginRequest.getUsername(), loginRequest.getPassword())).thenReturn(userProfile);

		UserProfile result = userController.login(loginRequest);
		verify(userService).verify(loginRequest.getUsername(), loginRequest.getPassword());

		assertEquals(userProfile, result);
	}

}
