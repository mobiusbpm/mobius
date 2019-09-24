package co.mobius.app.securitycontext.gateways.controllers;

import co.mobius.app.common.MobiusAppConstants;
import co.mobius.app.securitycontext.application.UserAppService;
import co.mobius.app.securitycontext.domain.model.UserModel;
import co.mobius.app.securitycontext.interfaces.ohs.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserResourceImpl implements UserResource {
	@Autowired
	private UserAppService userAppService;

	@Override
	@GetMapping("/loginname")
	public UserModel getUserByLoginName(@RequestParam("loginName") String loginName) {
		return userAppService.getUserByLoginName(loginName);
	}

	@Override
	@GetMapping("/loginnamelike")
	public List<UserModel> getUserByLoginNameLike(@RequestParam("loginName") String loginName) {
		return userAppService.getUserByLoginNameLike(MobiusAppConstants.PERCENT_SIGN
				+loginName
				+MobiusAppConstants.PERCENT_SIGN);
	}

	@Override
	@PostMapping("/login")
	public UserModel loginWithEmailAndPassword(@RequestBody UserModel userModel) {
		UserModel userModel1 = userAppService.loginWithEmailAndPassword(userModel.getEmail(), userModel.getPassword());
		return userModel1;
	}
}
