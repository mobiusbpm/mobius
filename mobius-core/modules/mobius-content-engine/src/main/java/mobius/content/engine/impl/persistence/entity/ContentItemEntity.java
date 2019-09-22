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
package mobius.content.engine.impl.persistence.entity;

import java.util.Date;

import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.content.api.ContentItem;

/**
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
public interface ContentItemEntity extends ContentItem, Entity {

    void setContentAvailable(boolean contentAvailable);

    void setContentSize(Long contentSize);

    void setCreated(Date created);

    void setLastModified(Date lastModified);
}