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
import mobius.idm.api.Token;
import mobius.idm.api.TokenQuery;
import mobius.idm.engine.impl.TokenQueryImpl;

/**
 *
 */
public interface TokenEntityManager extends EntityManager<TokenEntity> {

    Token createNewToken(String tokenId);

    void updateToken(Token updatedToken);

    boolean isNewToken(Token token);

    List<Token> findTokenByQueryCriteria(TokenQueryImpl query);

    long findTokenCountByQueryCriteria(TokenQueryImpl query);

    TokenQuery createNewTokenQuery();

    List<Token> findTokensByNativeQuery(Map<String, Object> parameterMap);

    long findTokenCountByNativeQuery(Map<String, Object> parameterMap);
}