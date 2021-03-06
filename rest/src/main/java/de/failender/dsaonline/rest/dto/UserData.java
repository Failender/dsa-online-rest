// Generated by delombok at Thu Nov 22 18:54:11 CET 2018
package de.failender.dsaonline.rest.dto;

import java.util.List;

public class UserData {
	public String name;
	public String token;
	public List<String> roles;
	public List<String> meister;
	public String gruppe;
	public String password;


	public String getName() {
		return this.name;
	}


	public String getToken() {
		return this.token;
	}


	public List<String> getRoles() {
		return this.roles;
	}


	public List<String> getMeister() {
		return this.meister;
	}


	public String getGruppe() {
		return this.gruppe;
	}


	public String getPassword() {
		return this.password;
	}


	public void setName(final String name) {
		this.name = name;
	}


	public void setToken(final String token) {
		this.token = token;
	}


	public void setRoles(final List<String> roles) {
		this.roles = roles;
	}


	public void setMeister(final List<String> meister) {
		this.meister = meister;
	}


	public void setGruppe(final String gruppe) {
		this.gruppe = gruppe;
	}


	public void setPassword(final String password) {
		this.password = password;
	}

	@Override

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof UserData)) return false;
		final UserData other = (UserData) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$name = this.getName();
		final Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final Object this$token = this.getToken();
		final Object other$token = other.getToken();
		if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
		final Object this$roles = this.getRoles();
		final Object other$roles = other.getRoles();
		if (this$roles == null ? other$roles != null : !this$roles.equals(other$roles)) return false;
		final Object this$meister = this.getMeister();
		final Object other$meister = other.getMeister();
		if (this$meister == null ? other$meister != null : !this$meister.equals(other$meister)) return false;
		final Object this$gruppe = this.getGruppe();
		final Object other$gruppe = other.getGruppe();
		if (this$gruppe == null ? other$gruppe != null : !this$gruppe.equals(other$gruppe)) return false;
		final Object this$password = this.getPassword();
		final Object other$password = other.getPassword();
		if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
		return true;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof UserData;
	}

	@Override

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final Object $token = this.getToken();
		result = result * PRIME + ($token == null ? 43 : $token.hashCode());
		final Object $roles = this.getRoles();
		result = result * PRIME + ($roles == null ? 43 : $roles.hashCode());
		final Object $meister = this.getMeister();
		result = result * PRIME + ($meister == null ? 43 : $meister.hashCode());
		final Object $gruppe = this.getGruppe();
		result = result * PRIME + ($gruppe == null ? 43 : $gruppe.hashCode());
		final Object $password = this.getPassword();
		result = result * PRIME + ($password == null ? 43 : $password.hashCode());
		return result;
	}

	@Override

	public java.lang.String toString() {
		return "UserData(name=" + this.getName() + ", token=" + this.getToken() + ", roles=" + this.getRoles() + ", meister=" + this.getMeister() + ", gruppe=" + this.getGruppe() + ", password=" + this.getPassword() + ")";
	}


	public UserData() {
	}


	public UserData(final String name, final String token, final List<String> roles, final List<String> meister, final String gruppe, final String password) {
		this.name = name;
		this.token = token;
		this.roles = roles;
		this.meister = meister;
		this.gruppe = gruppe;
		this.password = password;
	}
}
