package co.mobius.app.securitycontext.application;

import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User application service (Facade to ohs)
 */
@Service
public class UserAppService {
	@Autowired
	private UserRepository userRepository;

	public UserModel getUserByLoginName(String loginName){
		return	userRepository.getUserByLoginName(loginName);
	}

	public UserModel loginWithLoginNameAndPassword(String loginName, String password){
		return userRepository.getUserByLoginNameAndPassword(loginName, password);
	}
}
