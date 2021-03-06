package co.mobius.app.securitycontext.gateways.controllers;

import co.mobius.app.securitycontext.application.UserAppService;
import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.ohs.UserResource;
import mobius.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserResourceImpl implements UserResource {
	@Autowired
	private UserAppService userAppService;

	@Override
	@GetMapping("/loginname")
	public UserModel getUserByLoginName(String loginName) {
		return userAppService.getUserByLoginName(loginName);
	}

	@Override
	@PostMapping("/loginwithname")
	public UserModel loginWithLoginNameAndPassword(User user) {
		return userAppService.loginWithLoginNameAndPassword(user.getUserLoginName(), user.getUserPassword());
	}

}
