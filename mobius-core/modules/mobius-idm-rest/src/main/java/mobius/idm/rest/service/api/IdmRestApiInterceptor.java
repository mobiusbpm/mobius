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
package mobius.idm.rest.service.api;

import mobius.idm.api.Group;
import mobius.idm.api.GroupQuery;
import mobius.idm.api.Privilege;
import mobius.idm.api.PrivilegeQuery;
import mobius.idm.api.User;
import mobius.idm.api.UserQuery;

public interface IdmRestApiInterceptor {
    
    void accessGroupInfoById(Group group);
    
    void accessGroupInfoWithQuery(GroupQuery groupQuery);
    
    void createNewGroup(Group group);
    
    void deleteGroup(Group group);
    
    void accessUserInfoById(User user);
    
    void accessUserInfoWithQuery(UserQuery userQuery);
    
    void createNewUser(User user);
    
    void deleteUser(User user);
    
    void accessPrivilegeInfoById(Privilege privilege);
    
    void accessPrivilegeInfoWithQuery(PrivilegeQuery privilegeQuery);
    
    void addUserPrivilege(Privilege privilege, String userId);
    
    void addGroupPrivilege(Privilege privilege, String groupId);
    
    void deleteUserPrivilege(Privilege privilege, String userId);
    
    void deleteGroupPrivilege(Privilege privilege, String groupId);
    
    void accessIdmManagementInfo();
}
