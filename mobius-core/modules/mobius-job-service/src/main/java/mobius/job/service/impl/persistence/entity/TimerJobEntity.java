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
package mobius.job.service.impl.persistence.entity;

import java.util.Date;

/**
 * Stub of the common parts of a Job. You will normally work with a subclass of JobEntity, such as {@link TimerEntity} or {@link MessageEntity}.
 *
 * @author Tijs Rademakers
 *
 */
public interface TimerJobEntity extends AbstractRuntimeJobEntity {

    String getLockOwner();

    void setLockOwner(String claimedBy);

    Date getLockExpirationTime();

    void setLockExpirationTime(Date claimedUntil);
}
