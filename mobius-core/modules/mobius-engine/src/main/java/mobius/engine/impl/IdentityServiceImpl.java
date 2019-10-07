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
package mobius.engine.impl;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.identity.Authentication;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.engine.IdentityService;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.cmd.GetPotentialStarterGroupsCmd;
import mobius.engine.impl.cmd.GetPotentialStarterUsersCmd;
import mobius.engine.impl.util.EngineServiceUtil;
import mobius.idm.api.*;

import java.util.List;

/**
 *
 */
public class IdentityServiceImpl extends CommonEngineServiceImpl<ProcessEngineConfigurationImpl> implements IdentityService {

    public IdentityServiceImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public Group newGroup(String groupId) {
        return getIdmIdentityService().newGroup(groupId);
    }

    @Override
    public User newUser(String userId) {
        return getIdmIdentityService().newUser(userId);
    }

    @Override
    public void saveGroup(Group group) {
        getIdmIdentityService().saveGroup(group);
    }

    @Override
    public void saveUser(User user) {
        getIdmIdentityService().saveUser(user);
    }

    @Override
    public void updateUserPassword(User user) {
        getIdmIdentityService().updateUserPassword(user);
    }

    @Override
    public UserQuery createUserQuery() {
        return getIdmIdentityService().createUserQuery();
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        return getIdmIdentityService().createNativeUserQuery();
    }

    @Override
    public GroupQuery createGroupQuery() {
        return getIdmIdentityService().createGroupQuery();
    }

    @Override
    public NativeGroupQuery createNativeGroupQuery() {
        return getIdmIdentityService().createNativeGroupQuery();
    }

    @Override
    public List<Group> getPotentialStarterGroups(String processDefinitionId) {
        return commandExecutor.execute(new GetPotentialStarterGroupsCmd(processDefinitionId));
    }

    @Override
    public List<User> getPotentialStarterUsers(String processDefinitionId) {
        return commandExecutor.execute(new GetPotentialStarterUsersCmd(processDefinitionId));
    }

    @Override
    public void createMembership(String userId, String groupId) {
        getIdmIdentityService().createMembership(userId, groupId);
    }

    @Override
    public void deleteGroup(String groupId) {
        getIdmIdentityService().deleteGroup(groupId);
    }

    @Override
    public void deleteMembership(String userId, String groupId) {
        getIdmIdentityService().deleteMembership(userId, groupId);
    }

    @Override
    public boolean checkPassword(String userId, String password) {
        return getIdmIdentityService().checkPassword(userId, password);
    }

    @Override
    public void deleteUser(String userId) {
        getIdmIdentityService().deleteUser(userId);
    }

    @Override
    public void setUserPicture(String userId, Picture picture) {
        getIdmIdentityService().setUserPicture(userId, picture);
    }

    @Override
    public Picture getUserPicture(String userId) {
        return getIdmIdentityService().getUserPicture(userId);
    }

    @Override
    public void setAuthenticatedUserId(String authenticatedUserId) {
        Authentication.setAuthenticatedUserId(authenticatedUserId);
    }

    @Override
    public String getUserInfo(String userId, String key) {
        return getIdmIdentityService().getUserInfo(userId, key);
    }

    @Override
    public List<String> getUserInfoKeys(String userId) {
        return getIdmIdentityService().getUserInfoKeys(userId);
    }

    @Override
    public void setUserInfo(String userId, String key, String value) {
        getIdmIdentityService().setUserInfo(userId, key, value);
    }

    @Override
    public void deleteUserInfo(String userId, String key) {
        getIdmIdentityService().deleteUserInfo(userId, key);
    }
    
    protected IdmIdentityService getIdmIdentityService() {
        IdmIdentityService idmIdentityService = EngineServiceUtil.getIdmIdentityService(configuration);
        if (idmIdentityService == null) {
            throw new FlowableException("Trying to use idm identity service when it is not initialized");
        }
        return idmIdentityService;
    }

    @Override
    public User newUser(String loginName, String email) {
        return getIdmIdentityService().newUser(loginName, email);
    }
}
