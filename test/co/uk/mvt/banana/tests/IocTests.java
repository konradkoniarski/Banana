package co.uk.mvt.banana.tests;

import co.uk.mvt.banana.configuration.Logger;
import co.uk.mvt.banana.interfaces.Log;
import co.uk.mvt.banana.ioc.Container;
import co.uk.mvt.banana.tests.data.InterfaceA;
import junit.framework.TestCase;

import co.uk.mvt.banana.tests.data.ClassA;
import co.uk.mvt.banana.tests.data.ClassB;
import co.uk.mvt.banana.tests.data.InterfaceB;

public class IocTests extends TestCase {

    @Override
    protected void setUp() throws Exception {
        Container.INSTANCE.clearCache();
        Container.INSTANCE.clear();
    }

    public void testContainerNotInitializedGetInstaceByType() {
        try {
            Container.INSTANCE.getInstance(InterfaceA.class);
            fail();
        } catch (RuntimeException e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }

    public void testContainerNotInitializedGetInstanceByName() throws ClassNotFoundException {
        try {
            Container.INSTANCE.getInstance("InterfaceA");
            fail();
        } catch (RuntimeException e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }

    public void testNoDefaultLoggerSet() throws ClassNotFoundException {
        try {
            Container.INSTANCE.init();
            Container.INSTANCE.getInstance(InterfaceA.class);
            fail();
        } catch (RuntimeException e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }

    public void testNoClassDeclaration() throws ClassNotFoundException {
        Container.INSTANCE.set(Log.class, new Logger());
        Container.INSTANCE.init();
        Object interfaceA = Container.INSTANCE.getInstance(InterfaceA.class);
        assertNull(interfaceA);
    }

    public void testGetInstanceSimpleClassByType() throws ClassNotFoundException {
        Container.INSTANCE.set(Log.class, new Logger());
        Container.INSTANCE.set(InterfaceA.class, ClassA.class);
        Container.INSTANCE.init();
        Object interfaceA = Container.INSTANCE.getInstance(InterfaceA.class);
        assertNull(interfaceA);
    }

    public void testGetInstanceSimpleClassByName() throws ClassNotFoundException {
        Container.INSTANCE.set(Log.class, new Logger());
        Container.INSTANCE.set(InterfaceA.class, ClassA.class);
        Container.INSTANCE.init();
        Object interfaceA = Container.INSTANCE.getInstance(InterfaceA.class);
        assertNull(interfaceA);
    }

    public void testGetInstanceRecursive() throws ClassNotFoundException {
        Container.INSTANCE.set(Log.class, new Logger());
        Container.INSTANCE.set(InterfaceA.class, ClassA.class);
        Container.INSTANCE.set(InterfaceB.class, ClassB.class);

        Container.INSTANCE.init();
        Object interfaceB = Container.INSTANCE.getInstance("co.uk.mvt.banana.tests.data.InterfaceB");
        assertNull(interfaceB);
    }

    public void testGetInstanceRecursiveNotFound() throws ClassNotFoundException {
        Container.INSTANCE.set(Log.class, new Logger());
        Container.INSTANCE.set(InterfaceB.class, ClassB.class);

        Container.INSTANCE.init();
        Object interfaceB = Container.INSTANCE.getInstance(InterfaceB.class);
        assertNull(interfaceB);
    }
}