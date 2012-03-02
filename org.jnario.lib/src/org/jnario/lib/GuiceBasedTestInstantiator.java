/*******************************************************************************
 * Copyright (c) 2012 BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jnario.lib;


import com.google.inject.Guice;
import com.google.inject.util.Modules;

import org.jnario.runner.TestInstantiator;
/**
 * @author Sebastian Benz - Initial contribution and API
 */
public class GuiceBasedTestInstantiator implements TestInstantiator{

	@Override
	public Object createTest(Class<?> klass) throws Exception {
		return Guice.createInjector(Modules.EMPTY_MODULE).getInstance(klass);
	}

	@Override
	public void beforeTestRun() {
	}

	@Override
	public void afterTestRun() {
	}

}