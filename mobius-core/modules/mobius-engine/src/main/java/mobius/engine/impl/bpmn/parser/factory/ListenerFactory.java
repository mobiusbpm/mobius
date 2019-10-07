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
package mobius.engine.impl.bpmn.parser.factory;

import mobius.bpmn.model.EventListener;
import mobius.bpmn.model.FlowableListener;
import mobius.common.engine.api.delegate.event.FlowableEventListener;
import mobius.engine.delegate.CustomPropertiesResolver;
import mobius.engine.delegate.ExecutionListener;
import mobius.engine.delegate.TaskListener;
import mobius.engine.delegate.TransactionDependentExecutionListener;
import mobius.engine.delegate.TransactionDependentTaskListener;
import mobius.engine.impl.bpmn.parser.BpmnParse;
import mobius.engine.impl.bpmn.parser.BpmnParser;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;

/**
 * Factory class used by the {@link BpmnParser} and {@link BpmnParse} to instantiate the behaviour classes for {@link TaskListener} and {@link ExecutionListener} usages.
 * 
 * You can provide your own implementation of this class. This way, you can give different execution semantics to the standard construct.
 * 
 * The easiest and advisable way to implement your own {@link ListenerFactory} is to extend the {@link DefaultListenerFactory}.
 * 
 * An instance of this interface can be injected in the {@link ProcessEngineConfigurationImpl} and its subclasses.
 * 
 *
 * @author Yvo Swillens
 */
public interface ListenerFactory {

    TaskListener createClassDelegateTaskListener(FlowableListener listener);

    TaskListener createExpressionTaskListener(FlowableListener listener);

    TaskListener createDelegateExpressionTaskListener(FlowableListener listener);

    ExecutionListener createClassDelegateExecutionListener(FlowableListener listener);

    ExecutionListener createExpressionExecutionListener(FlowableListener listener);

    ExecutionListener createDelegateExpressionExecutionListener(FlowableListener listener);

    TransactionDependentExecutionListener createTransactionDependentDelegateExpressionExecutionListener(FlowableListener listener);

    FlowableEventListener createClassDelegateEventListener(EventListener eventListener);

    FlowableEventListener createDelegateExpressionEventListener(EventListener eventListener);

    FlowableEventListener createEventThrowingEventListener(EventListener eventListener);

    CustomPropertiesResolver createClassDelegateCustomPropertiesResolver(FlowableListener listener);

    CustomPropertiesResolver createExpressionCustomPropertiesResolver(FlowableListener listener);

    CustomPropertiesResolver createDelegateExpressionCustomPropertiesResolver(FlowableListener listener);

    TransactionDependentTaskListener createTransactionDependentDelegateExpressionTaskListener(FlowableListener listener);
}