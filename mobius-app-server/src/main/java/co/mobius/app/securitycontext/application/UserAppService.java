package co.mobius.app.securitycontext.application;

import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

	public List<UserModel> getUserByLoginNameLike(String loginName){
		return userRepository.getUserByLoginNameLike(loginName);
	}

	public UserModel loginWithEmailAndPassword(String email, String password) {
		return userRepository.getUserByEmailAndPassword(email, password);
	}
}
