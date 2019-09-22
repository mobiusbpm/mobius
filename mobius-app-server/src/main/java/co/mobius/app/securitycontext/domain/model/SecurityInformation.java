package co.mobius.app.securitycontext.domain.model;

import mobius.idm.api.Group;
import mobius.idm.api.User;

import java.util.List;

public class SecurityInformation {
	protected List<User> users;
	protected List<Group> groups;
	protected List<String> privileges;

	public SecurityInformation() {
	}

	public SecurityInformation(List<User> users, List<Group> groups, List<String> privileges) {
		this.users = users;
		this.groups = groups;
		this.privileges = privileges;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<String> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<String> privileges) {
		this.privileges = privileges;
	}
}
