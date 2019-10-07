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
package mobius.cmmn.engine.impl.persistence.entity;

import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.Entity;

import java.util.Date;

/**
 *
 */
public interface SentryPartInstanceEntity extends HasRevision, Entity {
    
    String getCaseDefinitionId();
    void setCaseDefinitionId(String caseDefinitionId);
    
    String getCaseInstanceId();
    void setCaseInstanceId(String caseInstanceId);
    
    String getPlanItemInstanceId();
    void setPlanItemInstanceId(String planItemId);
    
    String getOnPartId();
    void setOnPartId(String onPartId);
    
    String getIfPartId();
    void setIfPartId(String ifPartId);

    Date getTimeStamp();
    void setTimeStamp(Date timeStamp);
    
}
