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
package mobius.task.service.impl.persistence.entity.data.impl.cachematcher;

import java.util.Map;

import mobius.common.engine.impl.persistence.cache.CachedEntityMatcherAdapter;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 *
 */
public class TasksBySubScopeIdAndScopeTypeMatcher extends CachedEntityMatcherAdapter<TaskEntity> {

    @SuppressWarnings("unchecked")
    @Override
    public boolean isRetained(TaskEntity taskEntity, Object parameter) {
        String subScopeId = ((Map<String, String>) parameter).get("subScopeId");
        String scopeType = ((Map<String, String>) parameter).get("scopeType");
        return taskEntity.getSubScopeId() != null && taskEntity.getSubScopeId().equals(subScopeId)
                && taskEntity.getScopeType() != null && taskEntity.getScopeType().equals(scopeType);
    }

}