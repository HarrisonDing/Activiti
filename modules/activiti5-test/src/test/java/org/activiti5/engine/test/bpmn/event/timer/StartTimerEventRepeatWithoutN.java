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

package org.activiti5.engine.test.bpmn.event.timer;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.test.Deployment;
import org.activiti5.engine.delegate.event.ActivitiEvent;
import org.activiti5.engine.delegate.event.ActivitiEventListener;
import org.activiti5.engine.delegate.event.ActivitiEventType;
import org.activiti5.engine.impl.test.PluggableActivitiTestCase;

/**
 * @author Saeid Mirzaei
 * Test case for ACT-4066
 */

public class StartTimerEventRepeatWithoutN extends PluggableActivitiTestCase {

	protected long counter = 0;
	protected StartEventListener startEventListener;
	
	class StartEventListener implements ActivitiEventListener {

		@Override
		public void onEvent(ActivitiEvent event) {
			if (event.getType().equals(ActivitiEventType.TIMER_FIRED)) {
				counter++;
			}
		}

		@Override
		public boolean isFailOnException() {
			return false;
		}
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		startEventListener = new StartEventListener();
		org.activiti5.engine.impl.cfg.ProcessEngineConfigurationImpl activiti5ProcessEngineConfiguration = (org.activiti5.engine.impl.cfg.ProcessEngineConfigurationImpl) processEngineConfiguration.getActiviti5CompatibilityHandler().getRawProcessConfiguration();
		activiti5ProcessEngineConfiguration.getEventDispatcher().addEventListener(startEventListener);
	}
	
	

	@Override
  protected void tearDown() throws Exception {
	  org.activiti5.engine.impl.cfg.ProcessEngineConfigurationImpl activiti5ProcessEngineConfiguration = (org.activiti5.engine.impl.cfg.ProcessEngineConfigurationImpl) processEngineConfiguration.getActiviti5CompatibilityHandler().getRawProcessConfiguration();
    activiti5ProcessEngineConfiguration.getEventDispatcher().removeEventListener(startEventListener);
    super.tearDown();
  }



  @Deployment
	public void testStartTimerEventRepeatWithoutN() {
		counter = 0;
		
		try {
			waitForJobExecutorToProcessAllJobs(5500, 500);
			fail("job is finished sooner than expected");
		} catch (ActivitiException e) {
			assertTrue(e.getMessage().startsWith("time limit"));
			assertTrue(counter >= 2);
		}
	}

}