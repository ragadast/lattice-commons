/*
 * Copyright 2004 Senunkan Shinryuu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Sep 16, 2005
 *
 */
package org.latticesoft.state.impl;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestState implements Command {

	private String message;
	public TestState() {}
	public TestState(String message) {
		this.setMessage(message);
	}
	
	private static final Log log = LogFactory.getLog(TestState.class);
	/* (non-Javadoc)
	 * @see org.latticesoft.command.Command#execute(java.lang.Object)
	 */
	public Object execute(Object o) throws CommandException {
		if (log.isInfoEnabled()) { log.info(message); }
		return null;
	}

	public static void main(String[] args) {
		Command selector = new LoopSelector();
		
		StateImpl s = new StateImpl("State");
		StateImpl s1 = new StateImpl("State1");
		StateImpl s2 = new StateImpl("State2");
		StateImpl s3 = new StateImpl("State3");
		s.setSelector(selector);
		s1.setSelector(selector);
		s2.setSelector(selector);
		s3.setSelector(selector);
		

		s1.addInStateListener(new TestState("I am in state 1"));
		s2.addInStateListener(new TestState("I am in state 2"));
		s3.addInStateListener(new TestState("I am in state 3"));
		
		s1.addOnEnterListener(new TestState("I am entering state 1"));
		s2.addOnEnterListener(new TestState("I am entering state 2"));
		s3.addOnEnterListener(new TestState("I am entering state 3"));

		s1.addOnExitListener(new TestState("I am exiting state 1"));
		s2.addOnExitListener(new TestState("I am exiting state 2"));
		s3.addOnExitListener(new TestState("I am exiting state 3"));
		
		EventImpl e12 = new EventImpl("Event12");
		EventImpl e21 = new EventImpl("Event21");
		EventImpl e13 = new EventImpl("Event13");
		EventImpl e31 = new EventImpl("Event31");
		EventImpl e23 = new EventImpl("Event23");
		EventImpl e32 = new EventImpl("Event32");

		TransitionImpl t12 = new TransitionImpl();
		t12.setEvent(e12);
		t12.setCurrentState(s1);
		t12.setNextState(s2);
		s1.addTransition(t12);

		TransitionImpl t21 = new TransitionImpl();
		t21.setEvent(e21);
		t21.setCurrentState(s2);
		t21.setNextState(s1);
		//s2.addTransition(t21);

		TransitionImpl t13 = new TransitionImpl();
		t13.setEvent(e13);
		t13.setCurrentState(s1);
		t13.setNextState(s3);
		//s1.addTransition(t13);

		TransitionImpl t31 = new TransitionImpl();
		t31.setEvent(e31);
		t31.setCurrentState(s3);
		t31.setNextState(s1);
		//s3.addTransition(t31);

		TransitionImpl t23 = new TransitionImpl();
		t23.setEvent(e23);
		t23.setCurrentState(s2);
		t23.setNextState(s3);
		//s2.addTransition(t23);

		TransitionImpl t32 = new TransitionImpl();
		t32.setEvent(e32);
		t32.setCurrentState(s3);
		t32.setNextState(s2);
		//s3.addTransition(t32);


		// sub states
		StateImpl ss1 = new StateImpl("Sub State 1");
		StateImpl ss2 = new StateImpl("Sub State 2");
		StateImpl ss3 = new StateImpl("Sub State 3");
		ss1.setSelector(selector);
		ss2.setSelector(selector);
		ss3.setSelector(selector);

		ss1.addInStateListener(new TestState("  I am in sub state 1"));
		ss2.addInStateListener(new TestState("  I am in sub state 2"));
		ss3.addInStateListener(new TestState("  I am in sub state 3"));
		ss1.addOnEnterListener(new TestState("  I am entering sub state 1"));
		ss2.addOnEnterListener(new TestState("  I am entering sub state 2"));
		ss3.addOnEnterListener(new TestState("  I am entering sub state 3"));
		ss1.addOnExitListener(new TestState("  I am exiting sub state 1"));
		ss2.addOnExitListener(new TestState("  I am exiting sub state 2"));
		ss3.addOnExitListener(new TestState("  I am exiting sub state 3"));

		TransitionImpl tt12 = new TransitionImpl();
		tt12.setEvent(e21);
		tt12.setCurrentState(ss1);
		tt12.setNextState(ss2);
		//ss1.addTransition(tt12);
		TransitionImpl tt23 = new TransitionImpl();
		tt23.setEvent(e21);
		tt23.setCurrentState(ss2);
		tt23.setNextState(ss3);
		//ss2.addTransition(tt23);
		TransitionImpl tt31 = new TransitionImpl();
		tt31.setEvent(e21);
		tt31.setCurrentState(ss3);
		tt31.setNextState(ss1);
		//ss3.addTransition(tt31);

		s1.setCurrentState(ss1);
		
		
		log.info("========== Start ==========");
		log.info("Current state " + s1.getName());
		s.setCurrentState(s1);
		log.info("===== e12 =====");
		s.evaluate(e12);
		log.info("===== e21 =====");
		s.evaluate(e21);
		log.info("===== e13 =====");
		s.evaluate(e13);
		log.info("===== e31 =====");
		s.evaluate(e31);
		log.info("===== e21 =====");
		s.evaluate(e21);
		log.info("===== e21 =====");
		s.evaluate(e21);
		log.info("===== e21 =====");
		s.evaluate(e21);
		log.info("===== e21 =====");
		s.evaluate(e21);
		log.info("===== e12 =====");
		s.evaluate(e12);
		log.info("===== e23 =====");
		s.evaluate(e23);
		log.info("===== e31 =====");
		s.evaluate(e31);
		log.info("===== e31 =====");
		s.evaluate(e31);
		log.info("===== e21 =====");
		s.evaluate(e21);
		log.info("==========");
		
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return (this.message);
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}

