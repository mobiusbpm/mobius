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
package mobius.content.engine.impl;

import java.io.InputStream;

import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.content.api.ContentItem;
import mobius.content.api.ContentItemQuery;
import mobius.content.api.ContentService;
import mobius.content.engine.ContentEngineConfiguration;
import mobius.content.engine.impl.cmd.CreateContentItemCmd;
import mobius.content.engine.impl.cmd.DeleteContentItemCmd;
import mobius.content.engine.impl.cmd.DeleteContentItemsByScopeCmd;
import mobius.content.engine.impl.cmd.DeleteContentItemsCmd;
import mobius.content.engine.impl.cmd.GetContentItemStreamCmd;
import mobius.content.engine.impl.cmd.SaveContentItemCmd;

/**
 *
 */
public class ContentServiceImpl extends CommonEngineServiceImpl<ContentEngineConfiguration> implements ContentService {

    @Override
    public ContentItem newContentItem() {
        return commandExecutor.execute(new CreateContentItemCmd());
    }

    @Override
    public void saveContentItem(ContentItem contentItem) {
        commandExecutor.execute(new SaveContentItemCmd(contentItem));
    }

    @Override
    public void saveContentItem(ContentItem contentItem, InputStream inputStream) {
        commandExecutor.execute(new SaveContentItemCmd(contentItem, inputStream));
    }

    @Override
    public InputStream getContentItemData(String contentItemId) {
        return commandExecutor.execute(new GetContentItemStreamCmd(contentItemId));
    }

    @Override
    public void deleteContentItem(String contentItemId) {
        commandExecutor.execute(new DeleteContentItemCmd(contentItemId));
    }

    @Override
    public void deleteContentItemsByProcessInstanceId(String processInstanceId) {
        commandExecutor.execute(new DeleteContentItemsCmd(processInstanceId, null, null));
    }

    @Override
    public void deleteContentItemsByTaskId(String taskId) {
        commandExecutor.execute(new DeleteContentItemsCmd(null, taskId, null));
    }

    @Override
    public void deleteContentItemsByScopeIdAndScopeType(String scopeId, String scopeType) {
        commandExecutor.execute(new DeleteContentItemsByScopeCmd(scopeId, scopeType));
    }

    @Override
    public ContentItemQuery createContentItemQuery() {
        return new ContentItemQueryImpl(commandExecutor);
    }
}
