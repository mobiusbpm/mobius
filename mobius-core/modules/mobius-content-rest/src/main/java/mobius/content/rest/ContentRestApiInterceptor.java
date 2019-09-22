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
package mobius.content.rest;

import mobius.content.api.ContentItem;
import mobius.content.api.ContentItemQuery;
import mobius.content.rest.api.content.ContentItemQueryRequest;

public interface ContentRestApiInterceptor {
    
    void accessContentItemInfoById(ContentItem contentItem);
    
    void accessContentItemInfoWithQuery(ContentItemQuery contentItemQuery, ContentItemQueryRequest request);
    
    void createNewContentItem(ContentItem contentItem);
    
    void deleteContentItem(ContentItem contentItem);
    
    void accessContentManagementInfo();
}
