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
import mobius.idm.api.Picture;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Baeyens
 * @author Arkadiy Gornovoy
 * @author Joram Barrez
 */
public class UserEntityImpl extends AbstractIdmEngineEntity implements UserEntity, Serializable, HasRevision {

    private static final long serialVersionUID = 1L;

    protected String firstName;
    protected String lastName;
    protected String displayName;
    protected String tenantId;
    //mobius new
    protected String loginName;
    protected String password;
    protected String email;
    protected Integer statusCodeId;
    protected Integer authTypeCodeId;
    protected String createdBy;
    protected Instant createdTime;
    protected String updatedBy;
    protected Instant updatedTime;

    protected ByteArrayRef pictureByteArrayRef;

    public UserEntityImpl() {
    }

    @Override
    public Object getPersistentState() {
        Map<String, Object> persistentState = new HashMap<>();
        persistentState.put("firstName", firstName);
        persistentState.put("lastName", lastName);
        persistentState.put("displayName", displayName);
        persistentState.put("tenantId", tenantId);
        persistentState.put("loginName", loginName);
        persistentState.put("email", email);
        persistentState.put("password", password);
        persistentState.put("statusCodeId", statusCodeId);
        persistentState.put("authTypeCodeId", authTypeCodeId);
        persistentState.put("createdBy", createdBy);
        persistentState.put("createdTime", createdTime);
        persistentState.put("updatedBy", updatedBy);
        persistentState.put("updatedTime", updatedTime);

        if (pictureByteArrayRef != null) {
            persistentState.put("pictureByteArrayId", pictureByteArrayRef.getId());
        }

        return persistentState;
    }

    @Override
    public Picture getPicture() {
        if (pictureByteArrayRef != null && pictureByteArrayRef.getId() != null) {
            return new Picture(pictureByteArrayRef.getBytes(), pictureByteArrayRef.getName());
        }
        return null;
    }

    @Override
    public void setPicture(Picture picture) {
        if (picture != null) {
            savePicture(picture);
        } else {
            deletePicture();
        }
    }

    protected void savePicture(Picture picture) {
        if (pictureByteArrayRef == null) {
            pictureByteArrayRef = new ByteArrayRef();
        }
        pictureByteArrayRef.setValue(picture.getMimeType(), picture.getBytes());
    }

    protected void deletePicture() {
        if (pictureByteArrayRef != null) {
            pictureByteArrayRef.delete();
        }
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isPictureSet() {
        return pictureByteArrayRef != null && pictureByteArrayRef.getId() != null;
    }

    @Override
    public ByteArrayRef getPictureByteArrayRef() {
        return pictureByteArrayRef;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public void setStatusCodeId(Integer statusCodeId) {
        this.statusCodeId = statusCodeId;
    }

    @Override
    public Integer getStatusCodeId() {
        return this.statusCodeId;
    }

    @Override
    public String getLoginName() {
        return this.loginName;
    }

    @Override
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public Integer getAuthTypeCodeId() {
        return this.authTypeCodeId;
    }

    @Override
    public void setAuthTypeCodeId(Integer authTypeCodeId) {
        this.authTypeCodeId = authTypeCodeId;
    }


}