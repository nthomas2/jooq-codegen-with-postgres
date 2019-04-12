package com.nthomas.jooqcodegenwithpostgres.service;

import static com.nthomas.jooqcodegenwithpostgres.generated.tables.UserIdentities.USER_IDENTITIES;
import static com.nthomas.jooqcodegenwithpostgres.generated.tables.UserPasswords.USER_PASSWORDS;
import static com.nthomas.jooqcodegenwithpostgres.generated.tables.Users.USERS;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nthomas.jooqcodegenwithpostgres.generated.tables.records.UserIdentitiesRecord;
import com.nthomas.jooqcodegenwithpostgres.generated.tables.records.UsersRecord;
import com.nthomas.jooqcodegenwithpostgres.model.CreateUserRequest;
import com.nthomas.jooqcodegenwithpostgres.model.UserProfile;

@Service
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final DSLContext context;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(final DSLContext context, final PasswordEncoder passwordEncoder) {
		this.context = context;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserProfile createUser(CreateUserRequest createUserRequest) {
		if (checkIfUsernameTaken(createUserRequest.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
		}

		UsersRecord user = context.insertInto(USERS)
			.set(USERS.FIRSTNAME, createUserRequest.getFirstName())
			.set(USERS.LASTNAME, createUserRequest.getLastName())
			.returning()
			.fetchOne();

		UserIdentitiesRecord userIdentity = context.insertInto(USER_IDENTITIES)
			.set(USER_IDENTITIES.USER_ID, user.getId())
			.set(USER_IDENTITIES.NAME, createUserRequest.getUsername())
			.set(USER_IDENTITIES.TYPE, "EMAIL")
			.returning()
			.fetchOne();

		context.insertInto(USER_PASSWORDS)
			.set(USER_PASSWORDS.USER_ID, user.getId())
			.set(USER_PASSWORDS.PASSWORD, passwordEncoder.encode(createUserRequest.getPassword()))
			.execute();

		return userToUserProfile(user, userIdentity);
	}

	public Boolean checkIfUsernameTaken(String username) {
		return context.selectOne()
			.from(USERS)
			.join(USER_IDENTITIES).onKey()
			.where(USER_IDENTITIES.NAME.equalIgnoreCase(username))
			.fetchOptional()
			.isPresent();
	}

	public UserProfile userToUserProfile(UsersRecord user, UserIdentitiesRecord userIdentity) {
		UserProfile profile = new UserProfile();
		profile.setFirstName(user.getFirstname());
		profile.setLastName(user.getLastname());
		profile.setUserName(userIdentity.getName());

		return profile;
	}

	@Transactional(readOnly = true)
	public UserProfile verify(String username, String password) {

		Optional<Record> result = context.select()
			.from(USERS)
			.join(USER_PASSWORDS).on(USERS.ID.eq(USER_PASSWORDS.USER_ID))
			.join(USER_IDENTITIES).on(USERS.ID.eq(USER_IDENTITIES.USER_ID))
			.where(USER_IDENTITIES.NAME.equalIgnoreCase(username))
			.orderBy(USER_PASSWORDS.CREATED_AT.desc())
			.limit(1)
			.fetchOptional()
			;

		if (result.isEmpty() || !passwordEncoder.matches(password, result.get().get(USER_PASSWORDS.PASSWORD))) {
			LOG.info("Unsuccessful login for {}", username);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password invalid");
		}

		return userToUserProfile(result.get().into(USERS), result.get().into(USER_IDENTITIES));
	}
}
