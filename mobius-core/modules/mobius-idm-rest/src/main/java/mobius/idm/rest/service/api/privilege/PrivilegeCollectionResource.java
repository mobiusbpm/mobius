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
package mobius.idm.rest.service.api.privilege;

import static mobius.common.rest.api.PaginateListUtil.paginateList;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.rest.api.DataResponse;
import mobius.idm.api.Group;
import mobius.idm.api.IdmIdentityService;
import mobius.idm.api.Privilege;
import mobius.idm.api.PrivilegeQuery;
import mobius.idm.api.User;
import mobius.idm.rest.service.api.IdmRestApiInterceptor;
import mobius.idm.rest.service.api.IdmRestResponseFactory;
import mobius.idm.rest.service.api.group.GroupResponse;
import mobius.idm.rest.service.api.user.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 *
 */
@RestController
@Api(tags = { "Privileges" }, description = "Manage Privileges", authorizations = { @Authorization(value = "basicAuth") })
public class PrivilegeCollectionResource {
    
    @Autowired
    protected IdmIdentityService identityService;
    
    @Autowired
    protected IdmRestResponseFactory idmRestResponseFactory;
    
    @Autowired(required=false)
    protected IdmRestApiInterceptor restApiInterceptor;

    @ApiOperation(value = "List privileges", nickname="listPrivileges", tags = { "Privileges" }, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", dataType = "string", value = "Only return privileges with the given id", paramType = "query"),
            @ApiImplicitParam(name = "name", dataType = "string", value = "Only return privileges with the given name", paramType = "query"),
            @ApiImplicitParam(name = "userId", dataType = "string", value = "Only return privileges with the given userId", paramType = "query"),
            @ApiImplicitParam(name = "groupId", dataType = "string", value = "Only return privileges with the given groupId", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the requested privileges were returned.")
    })
    @RequestMapping(value = "/privileges", method = RequestMethod.GET)
    public DataResponse<PrivilegeResponse> getPrivileges(@ApiParam(hidden = true) @RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {
        PrivilegeQuery query = identityService.createPrivilegeQuery();
        
        if (allRequestParams.containsKey("id")) {
            query.privilegeId(allRequestParams.get("id"));
        }
        if (allRequestParams.containsKey("name")) {
            query.privilegeName(allRequestParams.get("name"));
        }
        if (allRequestParams.containsKey("userId")) {
            query.userId(allRequestParams.get("userId"));
        }
        if (allRequestParams.containsKey("groupId")) {
            query.groupId(allRequestParams.get("groupId"));
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessPrivilegeInfoWithQuery(query);
        }
        
        return paginateList(allRequestParams, query, "id", null, idmRestResponseFactory::createPrivilegeResponseList);
    }

    @ApiOperation(value = "List all users for a given privilege", nickname = "listPrivilegeUsers", tags = { "Privileges" }, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the privilege exists and its users are returned.")
    })
    @RequestMapping(value = "/privileges/{privilegeId}/users", method = RequestMethod.GET)
    public List<UserResponse> getUsers(@PathVariable String privilegeId) {
        Privilege privilege = getPrivilegeById(privilegeId);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessPrivilegeInfoById(privilege);
        }
        
        List<User> users = identityService.getUsersWithPrivilege(privilegeId);
        return idmRestResponseFactory.createUserResponseList(users, false);
    }

    @ApiOperation(value = "Deletes a privilege for a user", nickname = "deleteUserPrivilege", tags = { "Privileges" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the user privilege has been deleted")
    })
    @RequestMapping(value = "/privileges/{privilegeId}/users/{userId}", method = RequestMethod.DELETE)
    public void deleteUserPrivilege(@PathVariable String privilegeId, @PathVariable String userId) {
        Privilege privilege = getPrivilegeById(privilegeId);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.deleteUserPrivilege(privilege, userId);
        }
        
        identityService.deleteUserPrivilegeMapping(privilegeId, userId);
    }
    
    @ApiOperation(value = "Adds a privilege for a user", nickname = "addUserPrivilege", tags = { "Privileges" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the user privilege has been added")
    })
    @RequestMapping(value = "privileges/{privilegeId}/users", method = RequestMethod.POST)
    public void addUserPrivilege(@PathVariable String privilegeId, @RequestBody AddUserPrivilegeRequest request) {
        Privilege privilege = getPrivilegeById(privilegeId);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.addUserPrivilege(privilege, request.getUserId());
        }
        
        identityService.addUserPrivilegeMapping(privilegeId, request.getUserId());
    }
    
    @ApiOperation(value = "List all groups for a given privilege", nickname = "listPrivilegeGroups", tags = { "Privileges" }, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the privilege exists and its groups are returned.")
    })
    @RequestMapping(value = "/privileges/{privilegeId}/groups", method = RequestMethod.GET)
    public List<GroupResponse> getGroups(@PathVariable String privilegeId) {
        Privilege privilege = getPrivilegeById(privilegeId);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessPrivilegeInfoById(privilege);
        }
        
        List<Group> groups = identityService.getGroupsWithPrivilege(privilegeId);
        return idmRestResponseFactory.createGroupResponseList(groups);
    }

    @ApiOperation(value = "Deletes a privilege for a group", nickname = "deleteGroupPrivilege", tags = { "Privileges" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the group privilege has been deleted")
    })
    @RequestMapping(value = "/privileges/{privilegeId}/group/{groupId}", method = RequestMethod.DELETE)
    public void deleteGroupPrivilege(@PathVariable String privilegeId, @PathVariable String groupId) {
        Privilege privilege = getPrivilegeById(privilegeId);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.deleteGroupPrivilege(privilege, groupId);
        }
        
        identityService.deleteUserPrivilegeMapping(privilegeId, groupId);
    }
    
    @ApiOperation(value = "Adds a privilege for a group", nickname = "addGroupPrivilege", tags = { "Privileges" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the group privilege has been added")
    })
    @RequestMapping(value = "privileges/{privilegeId}/groups", method = RequestMethod.POST)
    public void addGroupPrivilege(@PathVariable String privilegeId, @RequestBody AddGroupPrivilegeRequest request) {
        Privilege privilege = getPrivilegeById(privilegeId);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.addGroupPrivilege(privilege, request.getGroupId());
        }
        
        identityService.addGroupPrivilegeMapping(privilegeId, request.getGroupId());
    }

    protected Privilege getPrivilegeById(String privilegeId) {
        Privilege privilege = identityService.createPrivilegeQuery().privilegeId(privilegeId).singleResult();
        
        if (privilege == null) {
            throw new FlowableObjectNotFoundException("Could not find privilege with id " + privilegeId, Privilege.class);
        }
        
        return privilege;
    }
}
