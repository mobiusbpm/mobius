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
package mobius.idm.engine.test.api.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mobius.idm.api.User;
import mobius.idm.api.UserQuery;
import mobius.idm.engine.test.ResourceFlowableIdmTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserQueryEscapeClauseTest extends ResourceFlowableIdmTestCase {

    public UserQueryEscapeClauseTest() {
        super("escapeclause/flowable.idm.cfg.xml");
    }

    @BeforeEach
    protected void setUp() throws Exception {
        createUser("kermit", "Kermit%", "Thefrog%", "kermit%@muppetshow.com");
        createUser("fozzie", "Fozzie_", "Bear_", "fozzie_@muppetshow.com");
    }

    private User createUser(String id, String firstName, String lastName, String email) {
        User user = idmIdentityService.newUser(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        idmIdentityService.saveUser(user);
        return user;
    }

    @AfterEach
    protected void tearDown() throws Exception {
        idmIdentityService.deleteUser("kermit");
        idmIdentityService.deleteUser("fozzie");
    }

    @Test
    public void testQueryByFirstNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstNameLike("%\\%%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("kermit", query.singleResult().getId());

        query = idmIdentityService.createUserQuery().userFirstNameLike("%\\_%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("fozzie", query.singleResult().getId());
    }

    @Test
    public void testQueryByLastNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userLastNameLike("%\\%%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("kermit", query.singleResult().getId());

        query = idmIdentityService.createUserQuery().userLastNameLike("%\\_%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("fozzie", query.singleResult().getId());
    }

    @Test
    public void testQueryByFullNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFullNameLike("%og\\%%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("kermit", query.singleResult().getId());

        query = idmIdentityService.createUserQuery().userFullNameLike("%it\\%%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("kermit", query.singleResult().getId());

        query = idmIdentityService.createUserQuery().userFullNameLike("%ar\\_%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("fozzie", query.singleResult().getId());

        query = idmIdentityService.createUserQuery().userFullNameLike("%ie\\_%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("fozzie", query.singleResult().getId());
    }

    @Test
    public void testQueryByEmailLike() {
        UserQuery query = idmIdentityService.createUserQuery().userEmailLike("%\\%%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("kermit", query.singleResult().getId());

        query = idmIdentityService.createUserQuery().userEmailLike("%\\_%");
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
        assertEquals("fozzie", query.singleResult().getId());
    }
}