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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.variable.api.types.VariableType;

/**
 *
 */
public interface VariableInstanceEntityManager extends EntityManager<VariableInstanceEntity> {

    VariableInstanceEntity create(String name, VariableType type, Object value);

    /**
     * Create a variable instance without setting the value on it.
     * <b>IMPORTANT:</b> If you use this method you would have to call {@link VariableInstanceEntity#setValue(Object)}
     * for setting the value
     * @param name the name of the variable to create
     * @param type the type of the creted variable
     *
     * @return the {@link VariableInstanceEntity} to be used
     */
    VariableInstanceEntity create(String name, VariableType type);

    List<VariableInstanceEntity> findVariableInstancesByTaskId(String taskId);

    List<VariableInstanceEntity> findVariableInstancesByTaskIds(Set<String> taskIds);

    List<VariableInstanceEntity> findVariableInstancesByExecutionId(String executionId);

    List<VariableInstanceEntity> findVariableInstancesByExecutionIds(Set<String> executionIds);

    VariableInstanceEntity findVariableInstanceByExecutionAndName(String executionId, String variableName);

    List<VariableInstanceEntity> findVariableInstancesByExecutionAndNames(String executionId, Collection<String> names);

    VariableInstanceEntity findVariableInstanceByTaskAndName(String taskId, String variableName);

    List<VariableInstanceEntity> findVariableInstancesByTaskAndNames(String taskId, Collection<String> names);
    
    List<VariableInstanceEntity> findVariableInstanceByScopeIdAndScopeType(String scopeId, String scopeType);
    
    VariableInstanceEntity findVariableInstanceByScopeIdAndScopeTypeAndName(String scopeId, String scopeType, String variableName);
    
    List<VariableInstanceEntity> findVariableInstancesByScopeIdAndScopeTypeAndNames(String scopeId, String scopeType, Collection<String> variableNames);
    
    List<VariableInstanceEntity> findVariableInstanceBySubScopeIdAndScopeType(String subScopeId, String scopeType);
    
    VariableInstanceEntity findVariableInstanceBySubScopeIdAndScopeTypeAndName(String subScopeId, String scopeType, String variableName);
    
    List<VariableInstanceEntity> findVariableInstancesBySubScopeIdAndScopeTypeAndNames(String subScopeId, String scopeType, Collection<String> variableNames);

    void deleteVariablesByTaskId(String taskId);

    void deleteVariablesByExecutionId(String executionId);
    
    void deleteByScopeIdAndScopeType(String scopeId, String scopeType);

}