package co.mobius.app.securitycontext.domain.model;

import mobius.idm.api.User;

/**
 * rich domain model for User
 */
public class UserModel {
	private User mbsUser;

	public UserModel(User mbsUser) {
		this.mbsUser = mbsUser;
	}
}
