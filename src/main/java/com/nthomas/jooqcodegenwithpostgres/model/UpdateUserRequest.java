package com.nthomas.jooqcodegenwithpostgres.model;

import javax.validation.constraints.NotBlank;

public class UpdateUserRequest extends CreateUserRequest {
	@NotBlank
	private String currentPassword;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
}
