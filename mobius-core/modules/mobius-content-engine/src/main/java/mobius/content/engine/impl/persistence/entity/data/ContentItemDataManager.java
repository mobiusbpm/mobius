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
package mobius.content.engine.impl.persistence.entity.data;

import java.util.List;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.content.api.ContentItem;
import mobius.content.engine.impl.ContentItemQueryImpl;
import mobius.content.engine.impl.persistence.entity.ContentItemEntity;

/**
 * @author Tijs Rademakers
 */
public interface ContentItemDataManager extends DataManager<ContentItemEntity> {

    long findContentItemCountByQueryCriteria(ContentItemQueryImpl contentItemQuery);

    List<ContentItem> findContentItemsByQueryCriteria(ContentItemQueryImpl contentItemQuery);

    void deleteContentItemsByTaskId(String taskId);

    void deleteContentItemsByProcessInstanceId(String processInstanceId);

    void deleteContentItemsByScopeIdAndScopeType(String scopeId, String scopeType);
}