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
import mobius.idm.api.Privilege;
import mobius.idm.api.PrivilegeQuery;
import mobius.idm.engine.IdmEngineConfiguration;
import mobius.idm.engine.impl.PrivilegeQueryImpl;
import mobius.idm.engine.impl.persistence.entity.data.PrivilegeDataManager;

/**
 *
 */
public class PrivilegeEntityManagerImpl extends AbstractEntityManager<PrivilegeEntity> implements PrivilegeEntityManager {

    protected PrivilegeDataManager privilegeDataManager;

    public PrivilegeEntityManagerImpl(IdmEngineConfiguration idmEngineConfiguration, PrivilegeDataManager privilegeDataManager) {
        super(idmEngineConfiguration);
        this.privilegeDataManager = privilegeDataManager;
    }

    @Override
    protected DataManager<PrivilegeEntity> getDataManager() {
        return privilegeDataManager;
    }

    @Override
    public PrivilegeQuery createNewPrivilegeQuery() {
        return new PrivilegeQueryImpl(getCommandExecutor());
    }

    @Override
    public List<Privilege> findPrivilegeByQueryCriteria(PrivilegeQueryImpl query) {
        return privilegeDataManager.findPrivilegeByQueryCriteria(query);
    }

    @Override
    public long findPrivilegeCountByQueryCriteria(PrivilegeQueryImpl query) {
        return privilegeDataManager.findPrivilegeCountByQueryCriteria(query);
    }

    @Override
    public List<Privilege> findPrivilegeByNativeQuery(Map<String, Object> parameterMap) {
        return privilegeDataManager.findPrivilegeByNativeQuery(parameterMap);
    }

    @Override
    public long findPrivilegeCountByNativeQuery(Map<String, Object> parameterMap) {
        return privilegeDataManager.findPrivilegeCountByNativeQuery(parameterMap);
    }

}
