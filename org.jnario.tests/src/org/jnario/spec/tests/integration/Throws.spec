package org.jnario.spec.tests.integration

import static extension org.jnario.jnario.test.util.SpecExecutor.*

describe "Throws"{
	
	fact "passes if exception is thrown"{
		'''
			package bootstrap
			
			import java.util.Stack
			import java.util.EmptyStackException
			
			describe "throws" {
			  fact new Stack<String>().pop throws EmptyStackException 
			}
		'''.executesSuccessfully
	} 
	
	fact "passes if exception of expected sub type is thrown"{
		'''
			package bootstrap
			
			import java.util.Stack
			
			describe "throws" {
			  fact new Stack<String>().pop throws Throwable 
			}
		'''.executesSuccessfully
	} 
	
	fact "fails if no exception is thrown"{
		'''
			package bootstrap
			
			describe "throws" {
			  fact 1 + 1 throws RuntimeException 
			}
		'''.executionFails
	} 
 
}