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
package mobius.cmmn.engine.impl.persistence.entity.data.impl;

import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.persistence.entity.CmmnDeploymentEntity;
import mobius.cmmn.engine.impl.persistence.entity.CmmnDeploymentEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.data.AbstractCmmnDataManager;
import mobius.cmmn.engine.impl.persistence.entity.data.CmmnDeploymentDataManager;
import mobius.cmmn.engine.impl.repository.CmmnDeploymentQueryImpl;

import java.util.List;

/**
 *
 */
public class MybatisCmmnDeploymentDataManager extends AbstractCmmnDataManager<CmmnDeploymentEntity> implements
		CmmnDeploymentDataManager {

    public MybatisCmmnDeploymentDataManager(CmmnEngineConfiguration cmmnEngineConfiguration) {
        super(cmmnEngineConfiguration);
    }

    @Override
    public Class<? extends CmmnDeploymentEntity> getManagedEntityClass() {
        return CmmnDeploymentEntityImpl.class;
    }

    @Override
    public CmmnDeploymentEntity create() {
        return new CmmnDeploymentEntityImpl();
    }

    @Override
    public CmmnDeploymentEntity findLatestDeploymentByName(String deploymentName) {
        List<?> list = getDbSqlSession().selectList("selectCmmnDeploymentsByName", deploymentName, 0, 1);
        if (list != null && !list.isEmpty()) {
            return (CmmnDeploymentEntity) list.get(0);
        }
        return null;
    }

    @Override
    public List<String> getDeploymentResourceNames(String deploymentId) {
        return getDbSqlSession().getSqlSession().selectList("selectCmmnResourceNamesByDeploymentId", deploymentId);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<CmmnDeployment> findDeploymentsByQueryCriteria(CmmnDeploymentQueryImpl deploymentQuery) {
        return getDbSqlSession().selectList("selectCmmnDeploymentsByQueryCriteria", deploymentQuery);
    }
    
    @Override
    public long findDeploymentCountByQueryCriteria(CmmnDeploymentQueryImpl deploymentQuery) {
        return (Long) getDbSqlSession().selectOne("selectCmmnDeploymentCountByQueryCriteria", deploymentQuery);
    }

}
