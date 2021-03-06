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

import java.util.ArrayList;
import java.util.List;

import mobius.common.rest.util.RestUrlBuilder;
import mobius.idm.api.Group;
import mobius.idm.api.Privilege;
import mobius.idm.api.User;
import mobius.idm.rest.service.api.group.GroupResponse;
import mobius.idm.rest.service.api.group.MembershipResponse;
import mobius.idm.rest.service.api.privilege.PrivilegeResponse;
import mobius.idm.rest.service.api.user.UserResponse;

/**
 *
 */
public class IdmRestResponseFactory {
    
    public List<UserResponse> createUserResponseList(List<User> users) {
        return createUserResponseList(users, false);
    }

    public List<UserResponse> createUserResponseList(List<User> users, boolean incudePassword) {
        RestUrlBuilder urlBuilder = createUrlBuilder();
        List<UserResponse> responseList = new ArrayList<>(users.size());
        for (User instance : users) {
            responseList.add(createUserResponse(instance, incudePassword, urlBuilder));
        }
        return responseList;
    }
    
    public UserResponse createUserResponse(User user, boolean incudePassword) {
        return createUserResponse(user, incudePassword, createUrlBuilder());
    }
    
    public UserResponse createUserResponse(User user, boolean incudePassword, RestUrlBuilder urlBuilder) {
        UserResponse response = new UserResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setDisplayName(user.getDisplayName());
        response.setId(user.getId());
        response.setEmail(user.getEmail());

        if (incudePassword) {
            response.setPassword(user.getPassword());
        }

        return response;
    }
    
    public List<GroupResponse> createGroupResponseList(List<Group> groups) {
        RestUrlBuilder urlBuilder = createUrlBuilder();
        List<GroupResponse> responseList = new ArrayList<>(groups.size());
        for (Group instance : groups) {
            responseList.add(createGroupResponse(instance, urlBuilder));
        }
        return responseList;
    }

    public GroupResponse createGroupResponse(Group group) {
        return createGroupResponse(group, createUrlBuilder());
    }

    public GroupResponse createGroupResponse(Group group, RestUrlBuilder urlBuilder) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setType(group.getType());
        return response;
    }

    public MembershipResponse createMembershipResponse(String userId, String groupId) {
        return createMembershipResponse(userId, groupId, createUrlBuilder());
    }

    public MembershipResponse createMembershipResponse(String userId, String groupId, RestUrlBuilder urlBuilder) {
        MembershipResponse response = new MembershipResponse();
        response.setGroupId(groupId);
        response.setUserId(userId);
        return response;
    }
    
    public List<PrivilegeResponse> createPrivilegeResponseList(List<Privilege> privileges) {
        List<PrivilegeResponse> responseList = new ArrayList<>(privileges.size());
        for (Privilege privilege : privileges) {
            responseList.add(createPrivilegeResponse(privilege));
        }
        return responseList;
    }
    
    public PrivilegeResponse createPrivilegeResponse(Privilege privilege) {
        return new PrivilegeResponse(privilege.getId(), privilege.getName());
    }
    
    public PrivilegeResponse createPrivilegeResponse(Privilege privilege, List<User> users, List<Group> groups) {
        PrivilegeResponse response = createPrivilegeResponse(privilege);
        
        List<UserResponse> userResponses = new ArrayList<>(users.size());
        for (User user : users) {
            userResponses.add(createUserResponse(user, false));
        }
        response.setUsers(userResponses);
        
        List<GroupResponse> groupResponses = new ArrayList<>(groups.size());
        for (Group group : groups) {
            groupResponses.add(createGroupResponse(group));
        }
        response.setGroups(groupResponses);
        
        return response;
    }
    
    protected RestUrlBuilder createUrlBuilder() {
        return RestUrlBuilder.fromCurrentRequest();
    }

}
