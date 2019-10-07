package co.mobius.app.securitycontext.gateways.persistence;

import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.repositories.UserRepository;
import mobius.engine.IdentityService;
import mobius.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * UserRepository implementation using Mobius Identity service
 */
@Component
public class UserRepositoryImpl implements UserRepository {
	@Autowired
	private IdentityService identityService;

	@Override
	public UserModel getUserByEmail(String email) {
		User user = identityService.createUserQuery().userEmail(email).singleResult();
		return new UserModel(user);
	}

	@Override
	public UserModel getUserByLoginName(String loginName) {
		User user = identityService.createUserQuery().userLoginName(loginName).singleResult();
		return new UserModel(user);
	}

	@Override
	public List<UserModel> getUserByLoginNameLike(String loginName) {
		List<User> userList = identityService.createUserQuery()
				.userLoginNameLike(loginName)
				.list();
		List<UserModel> userModels = new ArrayList<>(userList.size());
		for(User user:userList)
			userModels.add(new UserModel(user));
		return userModels;
	}

	@Override
	public UserModel getUserByEmailAndPassword(String email, String password) {
		User user = identityService.createUserQuery().userEmail(email).singleResult();
		if (identityService.checkPassword(user.getId(), password)){
			return new UserModel(user);
		}
		return null;
	}
	@Override
	public UserModel getUserById(Long userId) {
		User user = identityService.createUserQuery().userId(String.valueOf(userId)).singleResult();
		return new UserModel(user);
	}
	@Override
	public UserModel getUserByLoginNameAndPassword(String loginName, String password) {
		User user = identityService.createUserQuery().userLoginName(loginName).singleResult();
		if(identityService.checkPassword(user.getId(), user.getPassword())){
			return new UserModel(user);
		}
		return null;
	}
}
