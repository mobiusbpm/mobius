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
package mobius.cmmn.api.runtime;

import mobius.common.engine.api.query.Query;

/**
 *
 */
public interface SignalEventListenerInstanceQuery extends Query<SignalEventListenerInstanceQuery, SignalEventListenerInstance> {

    SignalEventListenerInstanceQuery id(String id);
    SignalEventListenerInstanceQuery caseInstanceId(String caseInstanceId);
    SignalEventListenerInstanceQuery caseDefinitionId(String caseDefinitionId);
    SignalEventListenerInstanceQuery elementId(String elementId);
    SignalEventListenerInstanceQuery planItemDefinitionId(String planItemDefinitionId);
    SignalEventListenerInstanceQuery name(String name);
    SignalEventListenerInstanceQuery stageInstanceId(String stageInstanceId);
    SignalEventListenerInstanceQuery stateAvailable();
    SignalEventListenerInstanceQuery stateSuspended();

    SignalEventListenerInstanceQuery orderByName();
    
}