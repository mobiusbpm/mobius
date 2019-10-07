package co.mobius.app.securitycontext.interfaces.ohs;

import co.mobius.app.securitycontext.domain.model.UserModel;
import mobius.idm.api.User;

/**
 * OHS for user resource
 */
public interface UserResource {
	UserModel getUserByLoginName(String loginName);
	UserModel loginWithLoginNameAndPassword(User user);
}
