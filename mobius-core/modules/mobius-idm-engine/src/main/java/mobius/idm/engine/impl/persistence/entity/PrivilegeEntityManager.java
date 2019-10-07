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
package mobius.idm.engine.impl.persistence.entity;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.idm.api.Privilege;
import mobius.idm.api.PrivilegeQuery;
import mobius.idm.engine.impl.PrivilegeQueryImpl;

/**
 *
 */
public interface PrivilegeEntityManager extends EntityManager<PrivilegeEntity> {

    PrivilegeQuery createNewPrivilegeQuery();

    List<Privilege> findPrivilegeByQueryCriteria(PrivilegeQueryImpl query);

    long findPrivilegeCountByQueryCriteria(PrivilegeQueryImpl query);

    List<Privilege> findPrivilegeByNativeQuery(Map<String, Object> parameterMap);

    long findPrivilegeCountByNativeQuery(Map<String, Object> parameterMap);

}
