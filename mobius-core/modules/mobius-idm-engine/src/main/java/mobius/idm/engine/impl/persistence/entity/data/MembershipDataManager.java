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
package mobius.idm.engine.impl.persistence.entity.data;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.idm.engine.impl.persistence.entity.MembershipEntity;

/**
 * @author Joram Barrez
 */
public interface MembershipDataManager extends DataManager<MembershipEntity> {

    void deleteMembership(String userId, String groupId);

    void deleteMembershipByGroupId(String groupId);

    void deleteMembershipByUserId(String userId);

}