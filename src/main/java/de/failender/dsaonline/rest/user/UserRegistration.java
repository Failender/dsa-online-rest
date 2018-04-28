package de.failender.dsaonline.rest.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class UserRegistration {
	private String name;
	private String password;
	private String token;
	private String gruppe;
}
