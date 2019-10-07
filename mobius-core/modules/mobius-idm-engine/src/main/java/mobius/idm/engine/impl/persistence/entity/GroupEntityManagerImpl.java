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

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.idm.api.Group;
import mobius.idm.api.GroupQuery;
import mobius.idm.api.event.FlowableIdmEventType;
import mobius.idm.engine.IdmEngineConfiguration;
import mobius.idm.engine.delegate.event.impl.FlowableIdmEventBuilder;
import mobius.idm.engine.impl.GroupQueryImpl;
import mobius.idm.engine.impl.persistence.entity.data.GroupDataManager;

/**
 *
 *
 */
public class GroupEntityManagerImpl extends AbstractEntityManager<GroupEntity> implements GroupEntityManager {

    protected GroupDataManager groupDataManager;

    public GroupEntityManagerImpl(IdmEngineConfiguration idmEngineConfiguration, GroupDataManager groupDataManager) {
        super(idmEngineConfiguration);
        this.groupDataManager = groupDataManager;
    }

    @Override
    protected DataManager<GroupEntity> getDataManager() {
        return groupDataManager;
    }

    @Override
    public Group createNewGroup(String groupId) {
        GroupEntity groupEntity = groupDataManager.create();
        groupEntity.setId(groupId);
        groupEntity.setRevision(0); // Needed as groups can be transient and not save when they are returned
        return groupEntity;
    }

    @Override
    public void delete(String groupId) {
        GroupEntity group = groupDataManager.findById(groupId);

        if (group != null) {

            getMembershipEntityManager().deleteMembershipByGroupId(groupId);
            if (getEventDispatcher() != null && getEventDispatcher().isEnabled()) {
                getEventDispatcher().dispatchEvent(FlowableIdmEventBuilder.createMembershipEvent(FlowableIdmEventType.MEMBERSHIPS_DELETED, groupId, null));
            }

            delete(group);
        }
    }

    @Override
    public GroupQuery createNewGroupQuery() {
        return new GroupQueryImpl(getCommandExecutor());
    }

    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query) {
        return groupDataManager.findGroupByQueryCriteria(query);
    }

    @Override
    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
        return groupDataManager.findGroupCountByQueryCriteria(query);
    }

    @Override
    public List<Group> findGroupsByUser(String userId) {
        return groupDataManager.findGroupsByUser(userId);
    }

    @Override
    public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap) {
        return groupDataManager.findGroupsByNativeQuery(parameterMap);
    }

    @Override
    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        return groupDataManager.findGroupCountByNativeQuery(parameterMap);
    }

    @Override
    public boolean isNewGroup(Group group) {
        return ((GroupEntity) group).getRevision() == 0;
    }

    @Override
    public List<Group> findGroupsByPrivilegeId(String privilegeId) {
        return groupDataManager.findGroupsByPrivilegeId(privilegeId);
    }

    public GroupDataManager getGroupDataManager() {
        return groupDataManager;
    }

    public void setGroupDataManager(GroupDataManager groupDataManager) {
        this.groupDataManager = groupDataManager;
    }

}
