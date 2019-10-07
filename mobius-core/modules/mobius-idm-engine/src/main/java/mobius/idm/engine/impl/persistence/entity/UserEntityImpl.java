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

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * map to mbs_user_account table
 */
public class UserEntityImpl extends AbstractIdmEngineEntity implements UserEntity, Serializable, HasRevision {

    private static final long serialVersionUID = 1L;

    //mobius
    protected String id;
    protected String userLoginName;
    protected String userPassword;
    protected Integer userStatusCodeId;
    protected Integer userAuthTypeCodeId;
    protected Long userCreatedBy;
    protected Instant userCreatedTime;
    protected Long userUpdatedBy;
    protected Instant userUpdatedTime;

    //    protected ByteArrayRef pictureByteArrayRef;

    public UserEntityImpl() {
    }

    @Override
    public Object getPersistentState() {
        Map<String, Object> persistentState = new HashMap<>();
        persistentState.put("userLoginName", userLoginName);
        persistentState.put("userPassword", userPassword);
        persistentState.put("userStatusCodeId", userStatusCodeId);
        persistentState.put("userAuthTypeCodeId", userAuthTypeCodeId);
        persistentState.put("userCreatedBy", userCreatedBy);
        persistentState.put("userCreatedTime", userCreatedTime);
        persistentState.put("userUpdatedBy", userUpdatedBy);
        persistentState.put("userUpdatedTime", userUpdatedTime);

    /*    if (pictureByteArrayRef != null) {
            persistentState.put("pictureByteArrayId", pictureByteArrayRef.getId());
        }*/

        return persistentState;
    }

   /* @Override
    public Picture getPicture() {
        if (pictureByteArrayRef != null && pictureByteArrayRef.getId() != null) {
            return new Picture(pictureByteArrayRef.getBytes(), pictureByteArrayRef.getName());
        }
        return null;
    }
*/
   /* @Override
    public void setPicture(Picture picture) {
        if (picture != null) {
            savePicture(picture);
        } else {
            deletePicture();
        }
    }*/

/*    protected void savePicture(Picture picture) {
        if (pictureByteArrayRef == null) {
            pictureByteArrayRef = new ByteArrayRef();
        }
        pictureByteArrayRef.setValue(picture.getMimeType(), picture.getBytes());
    }

    protected void deletePicture() {
        if (pictureByteArrayRef != null) {
            pictureByteArrayRef.delete();
        }
    }*/

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUserLoginName() {
        return this.userLoginName;
    }
    @Override
    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }

    @Override
    public String getUserPassword() {
        return this.userPassword;
    }
    @Override
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    @Override
    public Integer getUserStatusCodeId() {
        return this.userStatusCodeId;
    }
    @Override
    public void setUserStatusCodeId(Integer userStatusCodeId) {
        this.userStatusCodeId = userStatusCodeId;
    }
    @Override
    public Integer getUserAuthTypeCodeId() {
        return this.userAuthTypeCodeId;
    }
    @Override
    public void setUserAuthTypeCodeId(Integer userAuthTypeCodeId) {
        this.userAuthTypeCodeId = userAuthTypeCodeId;
    }
    @Override
    public Long getUserCreatedBy() {
        return this.userCreatedBy;
    }
    @Override
    public void setUserCreatedBy(Long userId) {
        this.userCreatedBy = userId;
    }
    @Override
    public Instant getUserCreatedTime() {
        return this.userCreatedTime;
    }
    @Override
    public void setUserCreatedTime(Instant userCreatedTime) {
        this.userCreatedTime = userCreatedTime;
    }
    @Override
    public Long getUserUpdatedBy() {
        return this.userUpdatedBy;
    }
    @Override
    public void setUserUpdatedBy(Long userUpdatedBy) {
        this.userUpdatedBy = userUpdatedBy;
    }
    @Override
    public Instant getUserUpdatedTime() {
        return this.userUpdatedTime;
    }
    @Override
    public void setUserUpdatedTime(Instant userUpdatedTime) {
        this.userUpdatedTime = userUpdatedTime;
    }
}
