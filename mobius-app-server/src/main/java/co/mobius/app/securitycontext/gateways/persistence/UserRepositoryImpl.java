package co.mobius.app.securitycontext.gateways.persistence;

import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.repositories.UserRepository;
import mobius.engine.IdentityService;
import mobius.idm.api.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl implements UserRepository {
	@Autowired
	private IdentityService identityService;

	@Override
	public UserModel getUserById(int userId) {
		return null;
	}

	@Override
	public UserModel getUserByEmail(String email) {
		UserModel userModel = null;
		User user = identityService.createUserQuery().userEmail(email).singleResult();
		if (user != null){
			userModel = new UserModel();
			BeanUtils.copyProperties(user, userModel);
		}
		return userModel;
	}

	@Override
	public UserModel getUserByLoginName(String loginName) {
		UserModel userModel = null;
		User user = identityService.createUserQuery().userLoginName(loginName).singleResult();
		if (user != null){
			userModel = new UserModel();
			BeanUtils.copyProperties(user, userModel);
		}
		return userModel;
	}

	Function<User, UserModel> userToUserModel = user -> {
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(user, userModel);
		return userModel;
	};

	@Override
	public List<UserModel> getUserByLoginNameLike(String loginName) {
		List<User> userList = identityService.createUserQuery()
				.userLoginNameLike(loginName)
				.list();
		return userList.stream().map(userToUserModel).collect(Collectors.toList());
	}

	@Override
	public UserModel getUserByEmailAndPassword(String email, String password) {
		UserModel userModel = null;
		User user = identityService.createUserQuery().userEmail(email).singleResult();
		if (identityService.checkPassword(user.getId(), password)){
			userModel = new UserModel();
			BeanUtils.copyProperties(user, userModel);
		}
		return userModel;
	}
}
