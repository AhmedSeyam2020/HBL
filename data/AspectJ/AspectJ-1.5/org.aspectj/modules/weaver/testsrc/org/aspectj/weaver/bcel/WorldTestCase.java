/* *******************************************************************
 * Copyright (c) 2002 Palo Alto Research Center, Incorporated (PARC).
 * All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 * Contributors: 
 *     PARC     initial implementation 
 * ******************************************************************/


package org.aspectj.weaver.bcel;

import java.lang.reflect.Modifier;

import org.aspectj.weaver.*;

/**
 * This is a test case for the nameType parts of worlds.
 */
public class WorldTestCase extends AbstractWorldTestCase {

    public WorldTestCase(String name) {
        super(name);
    }
        
    private final BcelWorld world 
        = new BcelWorld(BcweaverTests.TESTDATA_PATH + "/tracing.jar");

	protected World getWorld() {
		return world;
	}

	// XXX fix the various XXXs before expecting this test to work
    public void xtestTraceJar() {
        ResolvedType trace = world.resolve(UnresolvedType.forName("Trace"),true);
        assertTrue("Couldnt find type Trace",!trace.isMissing());
        fieldsTest(trace, Member.NONE);
        /*Member constr = */MemberImpl.methodFromString("void Trace.<init>()"); 
        //XXX need attribute fix - 
        //methodsTest(trace, new Member[] { constr });

        interfacesTest(trace, ResolvedType.NONE);
        superclassTest(trace, UnresolvedType.OBJECT);
        isInterfaceTest(trace, false);
        isClassTest(trace, false);
        isAspectTest(trace, true);

        pointcutsTest(trace, 
            new Member[] {
                MemberImpl.pointcut(trace, "traced", "(Ljava/lang/Object;)V"),
            });

        modifiersTest(trace.findPointcut("traced"), 
            Modifier.PUBLIC | Modifier.ABSTRACT);
        
        mungersTest(trace, 
            new ShadowMunger[] {
				world.shadowMunger("before(foo): traced(foo) -> void Trace.ajc_before_4(java.lang.Object))",
            					0),
				world.shadowMunger("afterReturning(foo): traced(foo) -> void Trace.ajc_afterreturning_3(java.lang.Object, java.lang.Object))",
            					Advice.ExtraArgument),
				world.shadowMunger("around(): execution(* doit(..)) -> java.lang.Object Trace.ajc_around_2(org.aspectj.runtime.internal.AroundClosure))",
            					Advice.ExtraArgument),
				world.shadowMunger("around(foo): traced(foo) -> java.lang.Object Trace.ajc_around_1(java.lang.Object, org.aspectj.runtime.internal.AroundClosure))",
            					Advice.ExtraArgument),
            });
        
        ResolvedType myTrace = world.resolve(UnresolvedType.forName("MyTrace"),true);
        assertTrue("Couldnt find type MyTrace",!myTrace.isMissing());

        interfacesTest(myTrace, ResolvedType.NONE);
        superclassTest(myTrace, trace);
        isInterfaceTest(myTrace, false);
        isClassTest(myTrace, false);
        isAspectTest(myTrace, true);

        //XXX need attribute fix - 
        //fieldsTest(myTrace, Member.NONE);


        pointcutsTest(trace, 
            new Member[] {
                MemberImpl.pointcut(trace, "traced", "(Ljava/lang/Object;)V"),
            });

        modifiersTest(myTrace.findPointcut("traced"), 
            Modifier.PUBLIC);
        
        // this tests for declared mungers
        mungersTest(myTrace, ShadowMunger.NONE);        
        
    }

    public void testIterator() {
        int abstractPublic = Modifier.ABSTRACT | Modifier.PUBLIC;
        ResolvedType iter = world.getCoreType(UnresolvedType.forRawTypeName("java.util.Iterator"));
      
        modifiersTest(iter, abstractPublic | Modifier.INTERFACE);
        fieldsTest(iter, ResolvedMember.NONE);
        methodsTest(iter, 
            new Member[] {
                MemberImpl.method(iter, 0, "hasNext", "()Z"),
                MemberImpl.method(iter, 0, "remove", "()V"),
                MemberImpl.method(iter, 0, "next", "()Ljava/lang/Object;"),
                });
        ResolvedMember remove = iter.lookupMethod(MemberImpl.method(iter, 0, "remove", "()V"));
        assertNotNull("iterator doesn't have remove" , remove);
        modifiersTest(remove, abstractPublic | Modifier.INTERFACE);
        exceptionsTest(remove, UnresolvedType.NONE);

        ResolvedMember clone = iter.lookupMethod(MemberImpl.method(UnresolvedType.OBJECT, 0, "clone", "()Ljava/lang/Object;"));
        assertNotNull("iterator doesn't have clone" , clone);
        //AV: JRockit Object.clone() is not native.. corrupted test here:
        //modifiersTest(clone, Modifier.PROTECTED | Modifier.NATIVE);
        assertTrue("should be protected" + clone.toString(), clone.isProtected());
        exceptionsTest(clone, UnresolvedType.forNames(new String[] {"java.lang.CloneNotSupportedException"}));

        interfacesTest(iter, ResolvedType.NONE);
        superclassTest(iter, UnresolvedType.OBJECT);
        pointcutsTest(iter, ResolvedMember.NONE);
        mungersTest(iter, ShadowMunger.NONE);
        isInterfaceTest(iter, true);
        isClassTest(iter, false);
        isAspectTest(iter, false);
    }

	public void testObjectCoersion() {
		assertCouldBeCoercibleFrom("java.lang.Object", "java.lang.String");
		assertCouldBeCoercibleFrom("java.lang.Integer", "java.lang.Object");
		assertCouldBeCoercibleFrom("java.io.Serializable", "java.lang.Runnable");		
		assertCouldBeCoercibleFrom("java.util.Stack", "java.lang.Runnable");		
		assertCouldNotBeCoercibleFrom("java.lang.Runnable", "java.lang.Integer");				
		assertCouldNotBeCoercibleFrom("java.lang.Integer", "java.lang.String");			
		assertCouldNotBeCoercibleFrom("java.lang.Integer", "java.lang.Runnable");							
	}

	// ----

	private void assertCouldBeCoercibleFrom(String a, String b) {
		isCoerceableFromTest(world.resolve(a), world.resolve(b), true);
	}

	private void assertCouldNotBeCoercibleFrom(String a, String b) {
		isCoerceableFromTest(world.resolve(a), world.resolve(b), false);
	}	    


}
