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

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a user, used in {@link IdmIdentityService}.
 */
public interface User extends Serializable {

    String getId();

    void setId(String id);

   /* String getTenantId();

    void setTenantId(String tenantId);

    boolean isPictureSet();*/

    /*void setEmail(String email);

    String getEmail();

    String getPassword();

    void setPassword(String string);*/

    /**
     * mobius
     */

    Long getUserId();

    void setUserId(Long userId);

    String getUserLoginName();

    void setUserLoginName(String userLoginName);

    String getUserEmail();

    void setUserEmail(String userEmail);

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

    Long getUserLastUpdatedBy();

    void setUserLastUpdatedBy(Long userId);

    Instant getUserLastUpdatedTime();

    void setUserLastUpdatedTime(Instant userLastUpdatedTime);

}
