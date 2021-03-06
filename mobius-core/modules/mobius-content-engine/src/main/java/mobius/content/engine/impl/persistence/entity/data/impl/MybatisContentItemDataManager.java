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
package mobius.content.engine.impl.persistence.entity.data.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.content.api.ContentItem;
import mobius.content.engine.ContentEngineConfiguration;
import mobius.content.engine.impl.ContentItemQueryImpl;
import mobius.content.engine.impl.persistence.entity.ContentItemEntity;
import mobius.content.engine.impl.persistence.entity.ContentItemEntityImpl;
import mobius.content.engine.impl.persistence.entity.data.AbstractContentDataManager;
import mobius.content.engine.impl.persistence.entity.data.ContentItemDataManager;

/**
 *
 */
public class MybatisContentItemDataManager extends AbstractContentDataManager<ContentItemEntity> implements ContentItemDataManager {

    public MybatisContentItemDataManager(ContentEngineConfiguration contentEngineConfiguration) {
        super(contentEngineConfiguration);
    }

    @Override
    public Class<? extends ContentItemEntity> getManagedEntityClass() {
        return ContentItemEntityImpl.class;
    }

    @Override
    public ContentItemEntity create() {
        return new ContentItemEntityImpl();
    }

    @Override
    public long findContentItemCountByQueryCriteria(ContentItemQueryImpl contentItemQuery) {
        return (Long) getDbSqlSession().selectOne("selectContentItemCountByQueryCriteria", contentItemQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ContentItem> findContentItemsByQueryCriteria(ContentItemQueryImpl contentItemQuery) {
        final String query = "selectContentItemsByQueryCriteria";
        return getDbSqlSession().selectList(query, contentItemQuery);
    }

    @Override
    public void deleteContentItemsByTaskId(String taskId) {
        getDbSqlSession().delete("deleteContentItemsByTaskId", taskId, getManagedEntityClass());
    }

    @Override
    public void deleteContentItemsByProcessInstanceId(String processInstanceId) {
        getDbSqlSession().delete("deleteContentItemsByProcessInstanceId", processInstanceId, getManagedEntityClass());
    }

    @Override
    public void deleteContentItemsByScopeIdAndScopeType(String scopeId, String scopeType) {
        Map<String, String> params = new HashMap<>(2);
        params.put("scopeId", scopeId);
        params.put("scopeType", scopeType);
        getDbSqlSession().delete("deleteContentItemsByScopeIdAndScopeType", params, getManagedEntityClass());
    }
}
