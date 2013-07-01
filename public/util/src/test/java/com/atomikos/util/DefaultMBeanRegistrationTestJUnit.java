package com.atomikos.util;

import static org.junit.Assert.*;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

public class DefaultMBeanRegistrationTestJUnit {
	
	private ObjectName instanceSpecificObjectName;
	
	private DefaultMBeanRegistration instance;
	private MBeanServer mockedMBeanServer;

	@Before
	public void setUp() throws Exception {
		instanceSpecificObjectName = new ObjectName("name:name=instance");
		instance = new DefaultMBeanRegistration() {	
			@Override
			protected ObjectName createObjectName() {
				return instanceSpecificObjectName;
			}
		};
		mockedMBeanServer = Mockito.mock(MBeanServer.class);
		Mockito.when(mockedMBeanServer.isRegistered(Mockito.any(ObjectName.class))).thenReturn(true);
	}

	@Test
	public void testPreRegisterWithSuppliedObjectName() throws Exception {
		ObjectName suppliedObjectName = new ObjectName("name:name=supplied");
		instance.preRegister(mockedMBeanServer, suppliedObjectName);
		assertEquals(suppliedObjectName, instance.getObjectName());
		assertEquals(mockedMBeanServer, instance.getMBeanServer());
	}

	@Test
	public void testPreRegisterWithNullObjectName() throws Exception {
		instance.preRegister(mockedMBeanServer, instanceSpecificObjectName);
		assertEquals(instanceSpecificObjectName, instance.getObjectName());
		assertEquals(mockedMBeanServer, instance.getMBeanServer());
	}
	
	@Test
	public void testUnregister() throws Exception {
		instance.preRegister(mockedMBeanServer, instanceSpecificObjectName);
		instance.unregister();
		assertUnregistered();
	}
	
	@Test
	public void testUnregisterDoesNothingWithoutPreRegister() throws Exception {
		instance.unregister();
		assertNotUnregistered();
	}
	
	@Test
	public void testUnregisterDoesNothingIfNotRegistered() throws Exception {
		instance.preRegister(mockedMBeanServer, instanceSpecificObjectName);
		Mockito.when(mockedMBeanServer.isRegistered(instanceSpecificObjectName)).thenReturn(false);
		instance.unregister();
		assertNotUnregistered();
	}

	private void assertNotUnregistered() throws MBeanRegistrationException, InstanceNotFoundException {
		Mockito.verify(mockedMBeanServer, Mockito.times(0)).unregisterMBean(Mockito.any(ObjectName.class));
	}

	private void assertUnregistered() throws MBeanRegistrationException, InstanceNotFoundException {
		Mockito.verify(mockedMBeanServer).unregisterMBean(instanceSpecificObjectName);
	}
}
