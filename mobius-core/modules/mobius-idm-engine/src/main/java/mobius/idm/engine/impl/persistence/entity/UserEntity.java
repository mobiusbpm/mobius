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
package mobius.idm.engine.impl.persistence.entity;

import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.idm.api.Picture;
import mobius.idm.api.User;

/**
 * @author Regi
 */
public interface UserEntity extends User, Entity, HasRevision {

    Picture getPicture();

    void setPicture(Picture picture);

    @Override
    String getId();

    @Override
    void setId(String id);

    @Override
    String getFirstName();

    @Override
    void setFirstName(String firstName);

    @Override
    String getLastName();

    @Override
    void setLastName(String lastName);
    
    @Override
    String getDisplayName();

    @Override
    void setDisplayName(String displayName);

    @Override
    String getEmail();

    @Override
    void setEmail(String email);

    @Override
    String getPassword();

    @Override
    void setPassword(String password);

    @Override
    boolean isPictureSet();

    ByteArrayRef getPictureByteArrayRef();

    @Override
    String getTenantId();

    @Override
    void setTenantId(String tenantId);

    @Override
    void setStatusCodeId(Integer statusCodeId) ;

    @Override
    Integer getStatusCodeId();

    @Override
    String getLoginName();

    @Override
    void setLoginName(String loginName);

    @Override
    Integer getAuthTypeCodeId();

    @Override
    void setAuthTypeCodeId(Integer authTypeCodeId);
}
