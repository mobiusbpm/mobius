package co.mobius.app.securitycontext.gateways.persistence;

import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.repositories.UserRepository;
import mobius.engine.IdentityService;
import mobius.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * UserRepository implementation using Mobius Identity service
 */
@Component
public class UserRepositoryImpl implements UserRepository {
	@Autowired
	private IdentityService identityService;


	@Override
	public UserModel getUserByLoginName(String loginName) {
		User user = identityService.createUserQuery().userLoginName(loginName).singleResult();
		return new UserModel(user);
	}


	@Override
	public UserModel getUserById(String id) {
		User user = identityService.createUserQuery().userId(id).singleResult();
		return new UserModel(user);
	}

	@Override
	public UserModel getUserByLoginNameAndPassword(String loginName, String password) {
		User user = identityService.createUserQuery().userLoginName(loginName).singleResult();
		if(identityService.checkPassword(user.getId(), user.getUserPassword())){
			return new UserModel(user);
		}
		return null;
	}
}
