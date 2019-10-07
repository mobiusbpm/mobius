package co.mobius.app.securitycontext.interfaces.repositories;

import co.mobius.app.securitycontext.domain.model.UserModel;

public interface UserRepository {

	UserModel getUserByLoginName(String userLoginName);

	UserModel getUserById(String id);

	UserModel getUserByLoginNameAndPassword(String userLoginName, String password);
}
