package co.mobius.app.securitycontext.interfaces.repositories;

import co.mobius.app.securitycontext.domain.model.UserModel;

import java.util.List;

public interface UserRepository {
	UserModel getUserByEmail(String email);

	UserModel getUserByLoginName(String loginName);

	List<UserModel> getUserByLoginNameLike(String loginName);

	UserModel getUserById(int userId);

	UserModel getUserByEmailAndPassword(String email, String password);
}
