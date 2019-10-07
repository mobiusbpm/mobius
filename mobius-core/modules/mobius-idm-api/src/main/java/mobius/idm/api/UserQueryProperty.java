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

package mobius.idm.api;

import mobius.common.engine.api.query.QueryProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the possible properties that can be used by the {@link UserQuery}.
 * 
 *
 */
public class UserQueryProperty implements QueryProperty {

    private static final long serialVersionUID = 1L;

    private static final Map<String, UserQueryProperty> properties = new HashMap<>();

    public static final UserQueryProperty USER_ID = new UserQueryProperty("RES.ID_");
    public static final UserQueryProperty FIRST_NAME = new UserQueryProperty("RES.FIRST_");
    public static final UserQueryProperty LAST_NAME = new UserQueryProperty("RES.LAST_");
    public static final UserQueryProperty DISPLAY_NAME = new UserQueryProperty("RES.DISPLAY_NAME_");
    public static final UserQueryProperty EMAIL = new UserQueryProperty("RES.USER_EMAIL");

    private String name;

    public UserQueryProperty(String name) {
        this.name = name;
        properties.put(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    public static UserQueryProperty findByName(String propertyName) {
        return properties.get(propertyName);
    }

}
