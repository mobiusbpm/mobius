/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobius.spring.security;

import mobius.idm.api.User;

import java.io.Serializable;
import java.time.Instant;

/**
 * An immutable serializable implementation of {@link User}. This implementation allows mutation only for the password,
 * in order for it to be removed by Spring Security when the credentials are erased.
 *
 */
public class UserDto implements User, Serializable {

    private static final long serialVersionUID = 1L;
    protected final String id;
    protected final String userLoginName;
    protected final Integer userStatusCodeId;
    protected final Integer userAuthTypeCodeId;
    protected final String userPassword;
    protected final Long userCreatedBy;
    protected final Instant userCreatedTime;
    protected final Long userUpdatedBy;
    protected final Instant userUpdatedTime;

    public UserDto(String id, String userLoginName, Integer userStatusCodeId, Integer userAuthTypeCodeId, String userPassword,
        Long userCreatedBy, Instant userCreatedTime, Long userUpdatedBy, Instant userUpdatedTime) {
        this.id = id;
        this.userLoginName = userLoginName;
        this.userStatusCodeId = userStatusCodeId;
        this.userAuthTypeCodeId = userAuthTypeCodeId;
        this.userPassword = userPassword;
        this.userCreatedBy = userCreatedBy;
        this.userCreatedTime = userCreatedTime;
        this.userUpdatedBy = userUpdatedBy;
        this.userUpdatedTime = userUpdatedTime;
    }
    public static UserDto create(User user) {
        return new UserDto(user.getId(), user.getUserLoginName(),user.getUserStatusCodeId(),
            user.getUserAuthTypeCodeId(),user.getUserPassword(),user.getUserCreatedBy(),user.getUserCreatedTime(),
            user.getUserUpdatedBy(),user.getUserUpdatedTime());
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getUserLoginName() {
        return userLoginName;
    }
    @Override
    public Integer getUserStatusCodeId() {
        return userStatusCodeId;
    }
    @Override
    public Integer getUserAuthTypeCodeId() {
        return userAuthTypeCodeId;
    }
    @Override
    public String getUserPassword() {
        return userPassword;
    }
    @Override
    public Long getUserCreatedBy() {
        return userCreatedBy;
    }
    @Override
    public Instant getUserCreatedTime() {
        return userCreatedTime;
    }
    @Override
    public Long getUserUpdatedBy() {
        return userUpdatedBy;
    }
    @Override
    public Instant getUserUpdatedTime() {
        return userUpdatedTime;
    }
    @Override
    public void setId(String id) {

    }
    @Override
    public void setUserLoginName(String userLoginName) {

    }

    @Override
    public void setUserPassword(String userPassword) {

    }
    @Override
    public void setUserStatusCodeId(Integer userStatusCodeId) {

    }
    @Override
    public void setUserAuthTypeCodeId(Integer userAuthTypeCodeId) {

    }
    @Override
    public void setUserCreatedBy(Long userId) {

    }
    @Override
    public void setUserCreatedTime(Instant userCreatedTime) {

    }
    @Override
    public void setUserUpdatedBy(Long userId) {

    }
    @Override
    public void setUserUpdatedTime(Instant userUpdatedTime) {

    }
}
