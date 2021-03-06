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
package mobius.dmn.api;

import java.io.InputStream;

import mobius.dmn.model.DmnDefinition;

/**
 * Builder for creating new deployments.
 * 
 * A builder instance can be obtained through {@link DmnRepositoryService#createDeployment()}.
 * 
 * Multiple resources can be added to one deployment before calling the {@link #deploy()} operation.
 * 
 * After deploying, no more changes can be made to the returned deployment and the builder instance can be disposed.
 * 
 *
 *
 */
public interface DmnDeploymentBuilder {

    DmnDeploymentBuilder addInputStream(String resourceName, InputStream inputStream);

    DmnDeploymentBuilder addClasspathResource(String resource);

    DmnDeploymentBuilder addString(String resourceName, String text);

    DmnDeploymentBuilder addDmnBytes(String resourceName, byte[] dmnBytes);

    DmnDeploymentBuilder addDmnModel(String resourceName, DmnDefinition dmnDefinition);

    /**
     * If called, no XML schema validation against the BPMN 2.0 XSD.
     * 
     * Not recommended in general.
     */
    DmnDeploymentBuilder disableSchemaValidation();

    /**
     * Gives the deployment the given name.
     */
    DmnDeploymentBuilder name(String name);

    /**
     * Gives the deployment the given category.
     */
    DmnDeploymentBuilder category(String category);

    /**
     * Gives the deployment the given tenant id.
     */
    DmnDeploymentBuilder tenantId(String tenantId);

    /**
     * Gives the deployment the given parent deployment id.
     */
    DmnDeploymentBuilder parentDeploymentId(String parentDeploymentId);

    DmnDeploymentBuilder enableDuplicateFiltering();

    /**
     * Deploys all provided sources to the DMN engine.
     */
    DmnDeployment deploy();

}
