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
 * @author Filip Hrisafov
 */
public class UserDto implements User, Serializable {

    private static final long serialVersionUID = 1L;

    protected final String id;
    protected String password;
    protected final String firstName;
    protected final String lastName;
    protected final String displayName;
    protected final String email;
    protected final String tenantId;

    protected final Long userId;
    protected final String userLoginName;
    protected final Integer userStatusCodeId;
    protected final Integer userAuthTypeCodeId;
    protected final String userPassword;
    protected final Long userCreatedBy;
    protected final Instant userCreatedTime;
    protected final Long userUpdatedBy;
    protected final Instant userUpdatedTime;

    public UserDto(String id, String password, String firstName, String lastName, String displayName, String email, String tenantId, Long userId,
        String userLoginName, Integer userStatusCodeId, Integer userAuthTypeCodeId, String userPassword, Long userCreatedBy, Instant userCreatedTime,
        Long userUpdatedBy, Instant userUpdatedTime) {
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.tenantId = tenantId;
        this.userId = userId;
        this.userLoginName = userLoginName;
        this.userStatusCodeId = userStatusCodeId;
        this.userAuthTypeCodeId = userAuthTypeCodeId;
        this.userPassword = userPassword;
        this.userCreatedBy = userCreatedBy;
        this.userCreatedTime = userCreatedTime;
        this.userUpdatedBy = userUpdatedBy;
        this.userUpdatedTime = userUpdatedTime;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getFirstName() {
        return firstName;
    }
    @Override
    public String getLastName() {
        return lastName;
    }
    @Override
    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public String getTenantId() {
        return tenantId;
    }
    @Override
    public Long getUserId() {
        return userId;
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
    public Long getUserUpdatedBy() {
        return userUpdatedBy;
    }
    public Instant getUserUpdatedTime() {
        return userUpdatedTime;
    }

    public static UserDto create(User user) {
        return new UserDto(
            user.getId(),
            user.getPassword(), user.getFirstName(), user.getLastName(), user.getDisplayName(), user.getEmail(), user.getTenantId(), user.getUserId(),
            user.getUserLoginName(), user.getUserStatusCodeId(), user.getUserAuthTypeCodeId(), user.getUserPassword(), user.getUserCreatedBy(),
            user.getUserCreatedTime(), user.getUserLastUpdatedBy(),
            user.getUserLastUpdatedTime());
    }

    @Override
    public void setId(String id) {

    }
    @Override
    public void setFirstName(String firstName) {

    }
    @Override
    public void setLastName(String lastName) {

    }
    @Override
    public void setDisplayName(String displayName) {

    }
    @Override
    public void setTenantId(String tenantId) {

    }
    @Override
    public boolean isPictureSet() {
        return false;
    }
    @Override
    public void setEmail(String email) {

    }
    @Override
    public void setPassword(String string) {

    }
    @Override
    public void setUserId(Long userId) {

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
    public Long getUserLastUpdatedBy() {
        return null;
    }
    @Override
    public void setUserLastUpdatedBy(Long userId) {

    }
    @Override
    public Instant getUserLastUpdatedTime() {
        return null;
    }
    @Override
    public void setUserLastUpdatedTime(Instant userLastUpdatedTime) {

    }
}
