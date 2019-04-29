package com.nthomas.jooqcodegenwithpostgres.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nthomas.jooqcodegenwithpostgres.model.CreateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UpdateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UserProfile;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

	@Autowired
	private UserService userService;

	private CreateUserRequest existingUser;

	@Before
	public void init() {
		existingUser = new CreateUserRequest();
		existingUser.setFirstName("Test");
		existingUser.setLastName("User");
		existingUser.setPassword("ThePassword");
		existingUser.setUsername("TheUsername");

		userService.createUser(existingUser);
	}

	@Test
	public void testCreateUser() {
		CreateUserRequest createUserRequest2 = new CreateUserRequest();
		createUserRequest2.setFirstName("AnotherTest");
		createUserRequest2.setLastName("AnotherUser");
		createUserRequest2.setPassword("AnotherPassword");
		createUserRequest2.setUsername("AnotherUser");

		UserProfile profile = userService.createUser(createUserRequest2);

		assertEquals(createUserRequest2.getFirstName(), profile.getFirstName());
		assertEquals(createUserRequest2.getLastName(), profile.getLastName());
		assertEquals(createUserRequest2.getUsername(), profile.getUserName());
	}

	@Test(expected = ResponseStatusException.class)
	public void testCreateUserUsernameTaken() {
		CreateUserRequest createUserRequest2 = new CreateUserRequest();
		createUserRequest2.setFirstName("AnotherTest");
		createUserRequest2.setLastName("AnotherUser");
		createUserRequest2.setPassword("ThePassword2");
		createUserRequest2.setUsername(existingUser.getUsername());

		userService.createUser(existingUser);

	}

	@Test
	public void testLogin() {
		UserProfile profile = userService.verify(existingUser.getUsername(), existingUser.getPassword());

		assertEquals(existingUser.getFirstName(), profile.getFirstName());
		assertEquals(existingUser.getLastName(), profile.getLastName());
		assertEquals(existingUser.getUsername(), profile.getUserName());
	}

	@Test(expected = ResponseStatusException.class)
	public void testLoginWrongPassword() {
		userService.verify(existingUser.getUsername(), "wrongPassword");
	}

	@Test(expected = ResponseStatusException.class)
	public void testLoginWrongUsername() {
		userService.verify("WrongUsername", existingUser.getPassword());
	}

	@Test
	public void testUpdateUser() throws InterruptedException {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest();
		updateUserRequest.setFirstName("NewFirst");
		updateUserRequest.setLastName("NewLast");
		updateUserRequest.setUsername(existingUser.getUsername());
		updateUserRequest.setCurrentPassword(existingUser.getPassword());
		updateUserRequest.setPassword("NewPassword");

		UserProfile profile = userService.updateUser(updateUserRequest);

		assertEquals(updateUserRequest.getFirstName(), profile.getFirstName());
		assertEquals(updateUserRequest.getLastName(), profile.getLastName());
		assertEquals(updateUserRequest.getUsername(), profile.getUserName());

		userService.verify(updateUserRequest.getUsername(), updateUserRequest.getPassword());
	}

	@Test(expected = ResponseStatusException.class)
	public void testUpdateUserWrongPassword() {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest();
		updateUserRequest.setFirstName("NewFirst");
		updateUserRequest.setLastName("NewLast");
		updateUserRequest.setUsername(existingUser.getUsername());
		updateUserRequest.setCurrentPassword("WrongPassword");
		updateUserRequest.setPassword("NewPassword");

		userService.updateUser(updateUserRequest);
	}

	@Test
	public void testUpdateUserNoPasswordChange() {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest();
		updateUserRequest.setFirstName("NewFirst");
		updateUserRequest.setLastName("NewLast");
		updateUserRequest.setUsername(existingUser.getUsername());
		updateUserRequest.setCurrentPassword(existingUser.getPassword());

		UserProfile profile = userService.updateUser(updateUserRequest);

		assertEquals(updateUserRequest.getFirstName(), profile.getFirstName());
		assertEquals(updateUserRequest.getLastName(), profile.getLastName());
		assertEquals(updateUserRequest.getUsername(), profile.getUserName());

		userService.verify(updateUserRequest.getUsername(), existingUser.getPassword());
	}

}
