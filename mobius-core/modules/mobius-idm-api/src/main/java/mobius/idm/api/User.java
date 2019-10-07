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
package mobius.idm.api;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a user, used in {@link IdmIdentityService}.
 */
public interface User extends Serializable {

    String getId();

    void setId(String id);

    String getUserLoginName();

    void setUserLoginName(String userLoginName);

    String getUserPassword();

    void setUserPassword(String userPassword);

    Integer getUserStatusCodeId();

    void setUserStatusCodeId(Integer userStatusCodeId);

    Integer getUserAuthTypeCodeId();

    void setUserAuthTypeCodeId(Integer userAuthTypeCodeId);

    Long getUserCreatedBy();

    void setUserCreatedBy(Long userId);

    Instant getUserCreatedTime();

    void setUserCreatedTime(Instant userCreatedTime);

    Long getUserUpdatedBy();

    void setUserUpdatedBy(Long userId);

    Instant getUserUpdatedTime();

    void setUserUpdatedTime(Instant userUpdatedTime);


    default String getPassword(){
        return getUserPassword();
    }

    default void setPassword(String password){
        setUserPassword(password);
    }

    default String getFirstName(){
        return StringUtils.EMPTY;
    }

    default void setFirstName(String firstName){
    }

    default String getLastName(){
        return StringUtils.EMPTY;
    }

    default void setLastName(String lastName){

    }

    default String getDisplayName(){
        return StringUtils.EMPTY;
    }

    default void setDisplayName(String displayName){

    }

    default String getEmail(){
        return StringUtils.EMPTY;
    }

    default void setEmail(String email){

    }

    default String getTenantId(){
        return StringUtils.EMPTY;
    }

    default void setTenantId(String tenantId){

    }
}
