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

package mobius.variable.service.impl.persistence.entity;

import java.util.Date;

import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.api.types.ValueFields;
import mobius.variable.api.types.VariableType;

/**
 * @author Christian Lipphardt (camunda)
 *
 */
public interface HistoricVariableInstanceEntity extends ValueFields, HistoricVariableInstance, Entity, HasRevision {

    VariableType getVariableType();

    void setName(String name);

    void setVariableType(VariableType variableType);

    void setProcessInstanceId(String processInstanceId);

    void setTaskId(String taskId);

    void setCreateTime(Date createTime);

    void setLastUpdatedTime(Date lastUpdatedTime);

    void setExecutionId(String executionId);
    
    void setScopeId(String scopeId);
    
    void setSubScopeId(String subScopeId);
    
    void setScopeType(String scopeType);

    VariableByteArrayRef getByteArrayRef();

}
