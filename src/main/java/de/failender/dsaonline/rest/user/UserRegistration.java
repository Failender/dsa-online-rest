package de.failender.dsaonline.rest.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistration {
	private String name;
	private String password;
	private String token;
	private String gruppe;
}
