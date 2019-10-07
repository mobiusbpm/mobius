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
package mobius.app.engine.impl.persistence.entity.data.impl;

import java.util.List;

import mobius.app.engine.impl.persistence.entity.AppDeploymentEntity;
import mobius.app.engine.impl.persistence.entity.AppDeploymentEntityImpl;
import mobius.app.engine.impl.persistence.entity.data.AbstractAppDataManager;
import mobius.app.engine.impl.persistence.entity.data.AppDeploymentDataManager;
import mobius.app.engine.impl.repository.AppDeploymentQueryImpl;
import mobius.app.api.repository.AppDeployment;
import mobius.app.engine.AppEngineConfiguration;

/**
 *
 */
public class MybatisAppDeploymentDataManager extends AbstractAppDataManager<AppDeploymentEntity>
		implements AppDeploymentDataManager {

    public MybatisAppDeploymentDataManager(AppEngineConfiguration cmmnEngineConfiguration) {
        super(cmmnEngineConfiguration);
    }

    @Override
    public Class<? extends AppDeploymentEntity> getManagedEntityClass() {
        return AppDeploymentEntityImpl.class;
    }

    @Override
    public AppDeploymentEntity create() {
        return new AppDeploymentEntityImpl();
    }

    @Override
    public AppDeploymentEntity findLatestDeploymentByName(String deploymentName) {
        List<?> list = getDbSqlSession().selectList("selectAppDeploymentsByName", deploymentName, 0, 1);
        if (list != null && !list.isEmpty()) {
            return (AppDeploymentEntity) list.get(0);
        }
        return null;
    }

    @Override
    public List<String> getDeploymentResourceNames(String deploymentId) {
        return getDbSqlSession().getSqlSession().selectList("selectAppResourceNamesByDeploymentId", deploymentId);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<AppDeployment> findDeploymentsByQueryCriteria(AppDeploymentQueryImpl deploymentQuery) {
        return getDbSqlSession().selectList("selectAppDeploymentsByQueryCriteria", deploymentQuery);
    }
    
    @Override
    public long findDeploymentCountByQueryCriteria(AppDeploymentQueryImpl deploymentQuery) {
        return (Long) getDbSqlSession().selectOne("selectAppDeploymentCountByQueryCriteria", deploymentQuery);
    }

}
