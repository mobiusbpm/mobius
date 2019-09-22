package co.mobius.app.securitycontext.interfaces.ohs;

import co.mobius.app.securitycontext.domain.model.UserModel;

import java.util.List;

/**
 * OHS for user resource
 */
public interface UserResource {
	UserModel getUserByLoginName(String loginName);
	List<UserModel> getUserByLoginNameLike(String loginName);
	UserModel loginWithEmailAndPassword(UserModel userModel);
}
