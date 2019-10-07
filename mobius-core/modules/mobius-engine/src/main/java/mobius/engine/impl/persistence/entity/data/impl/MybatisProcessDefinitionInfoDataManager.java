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
package mobius.engine.impl.persistence.entity.data.impl;

import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.persistence.entity.ProcessDefinitionInfoEntity;
import mobius.engine.impl.persistence.entity.ProcessDefinitionInfoEntityImpl;
import mobius.engine.impl.persistence.entity.data.AbstractProcessDataManager;
import mobius.engine.impl.persistence.entity.data.ProcessDefinitionInfoDataManager;

/**
 *
 */
public class MybatisProcessDefinitionInfoDataManager extends AbstractProcessDataManager<ProcessDefinitionInfoEntity> implements ProcessDefinitionInfoDataManager {

    public MybatisProcessDefinitionInfoDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public Class<? extends ProcessDefinitionInfoEntity> getManagedEntityClass() {
        return ProcessDefinitionInfoEntityImpl.class;
    }

    @Override
    public ProcessDefinitionInfoEntity create() {
        return new ProcessDefinitionInfoEntityImpl();
    }

    @Override
    public ProcessDefinitionInfoEntity findProcessDefinitionInfoByProcessDefinitionId(String processDefinitionId) {
        return (ProcessDefinitionInfoEntity) getDbSqlSession().selectOne("selectProcessDefinitionInfoByProcessDefinitionId", processDefinitionId);
    }
}
