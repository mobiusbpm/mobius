package co.mobius.app.securitycontext.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * rich domain model for User: extends from anaemic model in Mobius engine
 */
@Getter
@Setter
@NoArgsConstructor
public class UserModel  {
	private String loginName;
	private String password;
	private String email;
	private Integer statusCodeId;
	private Integer authTypeCodeId;
	private String createdBy;
	private Instant createdTime;
	private String updatedBy;
	private Instant updatedTime;
}
