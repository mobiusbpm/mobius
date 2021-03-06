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
package mobius.spring.test.authentication;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import mobius.idm.api.IdmIdentityService;
import mobius.idm.api.PasswordEncoder;
import mobius.idm.api.User;
import mobius.idm.engine.IdmEngineConfiguration;
import mobius.idm.engine.IdmEngines;
import mobius.idm.spring.authentication.SpringEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author faizal-manan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mobius/spring/test/engine/springIdmEngineWithPasswordEncoder-context.xml")
public class SpringPasswordEncoderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringPasswordEncoderTest.class);

    @Autowired
    private IdmEngineConfiguration autoWiredIdmIdmEngineConfiguration;
    
    @Autowired
    private IdmIdentityService autoWiredIdmIdentityService;

    @Test
    public void testSpringPasswordEncoderInstance() {
        PasswordEncoder passwordEncoder = autoWiredIdmIdmEngineConfiguration.getPasswordEncoder();

        autoWiredIdmIdmEngineConfiguration.setPasswordEncoder(new SpringEncoder(new StandardPasswordEncoder()));
        validatePassword();
        
        autoWiredIdmIdmEngineConfiguration.setPasswordEncoder(passwordEncoder);
    }

    @Test
    public void testValidateSpringPasswordEncoder() {
        PasswordEncoder passwordEncoder = autoWiredIdmIdmEngineConfiguration.getPasswordEncoder();

        IdmEngineConfiguration defaultIdmEngineConfiguration = IdmEngines.getDefaultIdmEngine().getIdmEngineConfiguration();

        assertThat(defaultIdmEngineConfiguration.getPasswordEncoder())
            .isInstanceOf(SpringEncoder.class)
            .isEqualTo(passwordEncoder);
        assertThat(passwordEncoder).isInstanceOfSatisfying(SpringEncoder.class,
            encoder -> assertThat(encoder.getSpringEncodingProvider()).isInstanceOf(BCryptPasswordEncoder.class));

    }

    private void validatePassword() {
        User user = autoWiredIdmIdentityService.newUser("johndoe");
        user.setPassword("xxx");
        autoWiredIdmIdentityService.saveUser(user);

        User johndoe = autoWiredIdmIdentityService.createUserQuery().userId("johndoe").list().get(0);
        LOGGER.info("Hash Password = {}", johndoe.getPassword());

        assertNotEquals("xxx", johndoe.getPassword());
        assertTrue(autoWiredIdmIdentityService.checkPassword("johndoe", "xxx"));
        assertFalse(autoWiredIdmIdentityService.checkPassword("johndoe", "invalid pwd"));

        autoWiredIdmIdentityService.deleteUser("johndoe");

    }
}
