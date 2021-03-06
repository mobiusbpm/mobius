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

package mobius.idm.engine.impl;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.common.engine.impl.query.AbstractQuery;
import mobius.idm.api.User;
import mobius.idm.api.UserQuery;
import mobius.idm.api.UserQueryProperty;
import mobius.idm.engine.impl.util.CommandContextUtil;

import java.util.List;

/**
 *
 */
public class UserQueryImpl extends AbstractQuery<UserQuery, User> implements UserQuery {

    private static final long serialVersionUID = 1L;
    protected String id;
    protected List<String> ids;
    protected String idIgnoreCase;
    protected String firstName;
    protected String firstNameLike;
    protected String firstNameLikeIgnoreCase;
    protected String lastName;
    protected String lastNameLike;
    protected String lastNameLikeIgnoreCase;
    protected String fullNameLike;
    protected String fullNameLikeIgnoreCase;
    protected String displayName;
    protected String displayNameLike;
    protected String displayNameLikeIgnoreCase;
    protected String groupId;
    protected List<String> groupIds;
    protected String tenantId;
    protected Integer userStatusCodeId;
    protected String userLoginNameLike;
    protected String userLoginName;

    public UserQueryImpl() {
    }

    public UserQueryImpl(CommandContext commandContext) {
        super(commandContext);
    }

    public UserQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }
    @Override
    public UserQuery userId(String id) {
        if (id == null) {
            throw new FlowableIllegalArgumentException("Provided id is null");
        }
        this.id = id;
        return this;
    }
    @Override
    public UserQuery userIds(List<String> ids) {
        if (ids == null) {
            throw new FlowableIllegalArgumentException("Provided ids is null");
        }
        this.ids = ids;
        return this;
    }

    @Override
    public UserQuery userIdIgnoreCase(String id) {
        if (id == null) {
            throw new FlowableIllegalArgumentException("Provided id is null");
        }
        this.idIgnoreCase = id.toLowerCase();
        return this;
    }

    @Override
    public UserQuery userFirstName(String firstName) {
        if (firstName == null) {
            throw new FlowableIllegalArgumentException("Provided first name is null");
        }
        this.firstName = firstName;
        return this;
    }

    @Override
    public UserQuery userFirstNameLike(String firstNameLike) {
        if (firstNameLike == null) {
            throw new FlowableIllegalArgumentException("Provided first name is null");
        }
        this.firstNameLike = firstNameLike;
        return this;
    }

    @Override
    public UserQuery userFirstNameLikeIgnoreCase(String firstNameLikeIgnoreCase) {
        if (firstNameLikeIgnoreCase == null) {
            throw new FlowableIllegalArgumentException("Provided first name is null");
        }
        this.firstNameLikeIgnoreCase = firstNameLikeIgnoreCase.toLowerCase();
        return this;
    }

    @Override
    public UserQuery userLastName(String lastName) {
        if (lastName == null) {
            throw new FlowableIllegalArgumentException("Provided last name is null");
        }
        this.lastName = lastName;
        return this;
    }

    @Override
    public UserQuery userLastNameLike(String lastNameLike) {
        if (lastNameLike == null) {
            throw new FlowableIllegalArgumentException("Provided last name is null");
        }
        this.lastNameLike = lastNameLike;
        return this;
    }

    @Override
    public UserQuery userLastNameLikeIgnoreCase(String lastNameLikeIgnoreCase) {
        if (lastNameLikeIgnoreCase == null) {
            throw new FlowableIllegalArgumentException("Provided last name is null");
        }
        this.lastNameLikeIgnoreCase = lastNameLikeIgnoreCase.toLowerCase();
        return this;
    }

    @Override
    public UserQuery userFullNameLike(String fullNameLike) {
        if (fullNameLike == null) {
            throw new FlowableIllegalArgumentException("Provided full name is null");
        }
        this.fullNameLike = fullNameLike;
        return this;
    }

    @Override
    public UserQuery userFullNameLikeIgnoreCase(String fullNameLikeIgnoreCase) {
        if (fullNameLikeIgnoreCase == null) {
            throw new FlowableIllegalArgumentException("Provided full name is null");
        }
        this.fullNameLikeIgnoreCase = fullNameLikeIgnoreCase.toLowerCase();
        return this;
    }
    
    @Override
    public UserQuery userDisplayName(String displayName) {
        if (displayName == null) {
            throw new FlowableIllegalArgumentException("Provided display name is null");
        }
        this.displayName = displayName;
        return this;
    }

    @Override
    public UserQuery userDisplayNameLike(String displayNameLike) {
        if (displayNameLike == null) {
            throw new FlowableIllegalArgumentException("Provided display name is null");
        }
        this.displayNameLike = displayNameLike;
        return this;
    }

    @Override
    public UserQuery userDisplayNameLikeIgnoreCase(String displayNameLikeIgnoreCase) {
        if (displayNameLikeIgnoreCase == null) {
            throw new FlowableIllegalArgumentException("Provided display name is null");
        }
        this.displayNameLikeIgnoreCase = displayNameLikeIgnoreCase.toLowerCase();
        return this;
    }

    @Override
    public UserQuery memberOfGroup(String groupId) {
        if (groupId == null) {
            throw new FlowableIllegalArgumentException("Provided groupId is null");
        }
        this.groupId = groupId;
        return this;
    }

    @Override
    public UserQuery memberOfGroups(List<String> groupIds) {
        if (groupIds == null) {
            throw new FlowableIllegalArgumentException("Provided groupIds is null");
        }
        this.groupIds = groupIds;
        return this;
    }

    @Override
    public UserQuery tenantId(String tenantId) {
        if (tenantId == null) {
            throw new FlowableIllegalArgumentException("TenantId is null");
        }
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public UserQuery statusCodeId(Integer userStatusCodeId) {
        if (userStatusCodeId == null) {
            throw new FlowableIllegalArgumentException("Provided userStatusCodeId is null");
        }
        this.userStatusCodeId = userStatusCodeId;
        return this;
    }

    @Override
    public UserQuery userLoginName(String userLoginName) {
        if (userLoginName == null) {
            throw new FlowableIllegalArgumentException("userLoginName is null");
        }
        this.userLoginName = userLoginName;
        return this;
    }

    @Override
    public UserQuery userLoginNameLike(String userLoginNameLike) {
        if (userLoginNameLike == null) {
            throw new FlowableIllegalArgumentException("Provided login name is null");
        }
        this.userLoginNameLike = userLoginNameLike;
        return this;
    }

    // sorting //////////////////////////////////////////////////////////

    @Override
    public UserQuery orderByUserId() {
        return orderBy(UserQueryProperty.USER_ID);
    }

    @Override
    public UserQuery orderByUserFirstName() {
        return orderBy(UserQueryProperty.FIRST_NAME);
    }

    @Override
    public UserQuery orderByUserLastName() {
        return orderBy(UserQueryProperty.LAST_NAME);
    }

    @Override
    public UserQuery orderByUserCreatedTime() {
        return orderBy(UserQueryProperty.USER_CREATED_TIME);
    }
    @Override
    public UserQuery orderByUserUpdatedTime() {
        return orderBy(UserQueryProperty.USER_UPDATED_TIME);
    }

    // results //////////////////////////////////////////////////////////

    @Override
    public long executeCount(CommandContext commandContext) {
        return CommandContextUtil.getUserEntityManager(commandContext).findUserCountByQueryCriteria(this);
    }

    @Override
    public List<User> executeList(CommandContext commandContext) {
        return CommandContextUtil.getUserEntityManager(commandContext).findUserByQueryCriteria(this);
    }

    // getters //////////////////////////////////////////////////////////

    public String getId() {
        return id;
    }
    public List<String> getIds() {
        return ids;
    }
    public String getIdIgnoreCase() {
        return idIgnoreCase;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getFirstNameLike() {
        return firstNameLike;
    }
    public String getFirstNameLikeIgnoreCase() {
        return firstNameLikeIgnoreCase;
    }
    public String getLastName() {
        return lastName;
    }
    public String getLastNameLike() {
        return lastNameLike;
    }
    public String getLastNameLikeIgnoreCase() {
        return lastNameLikeIgnoreCase;
    }
    public String getFullNameLike() {
        return fullNameLike;
    }
    public String getFullNameLikeIgnoreCase() {
        return fullNameLikeIgnoreCase;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getDisplayNameLike() {
        return displayNameLike;
    }
    public String getDisplayNameLikeIgnoreCase() {
        return displayNameLikeIgnoreCase;
    }
    public String getGroupId() {
        return groupId;
    }
    public List<String> getGroupIds() {
        return groupIds;
    }
    public String getTenantId() {
        return tenantId;
    }
    public Integer getUserStatusCodeId() {
        return userStatusCodeId;
    }
    public String getUserLoginNameLike() {
        return userLoginNameLike;
    }
    public String getUserLoginName() {
        return userLoginName;
    }

    @Override
    public UserQuery userEmail(String email) {
        return null;
    }
    @Override
    public UserQuery userEmailLike(String emailLike) {
        return null;
    }
    @Override
    public UserQuery orderByUserEmail() {
        return null;
    }
}
