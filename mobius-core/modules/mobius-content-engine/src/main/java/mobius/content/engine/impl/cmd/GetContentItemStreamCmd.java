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
package mobius.content.engine.impl.cmd;

import java.io.InputStream;
import java.io.Serializable;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.content.api.ContentItem;
import mobius.content.api.ContentObject;
import mobius.content.api.ContentStorage;
import mobius.content.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class GetContentItemStreamCmd implements Command<InputStream>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String contentItemId;

    public GetContentItemStreamCmd(String contentItemId) {
        this.contentItemId = contentItemId;
    }

    @Override
    public InputStream execute(CommandContext commandContext) {
        if (contentItemId == null) {
            throw new FlowableIllegalArgumentException("contentItemId is null");
        }

        ContentItem contentItem = CommandContextUtil.getContentItemEntityManager().findById(contentItemId);
        if (contentItem == null) {
            throw new FlowableObjectNotFoundException("content item could not be found with id " + contentItemId);
        }

        ContentStorage contentStorage = CommandContextUtil.getContentEngineConfiguration().getContentStorage();
        ContentObject contentObject = contentStorage.getContentObject(contentItem.getContentStoreId());
        return contentObject.getContent();
    }

}
