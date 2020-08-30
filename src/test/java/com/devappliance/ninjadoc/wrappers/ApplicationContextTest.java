package com.devappliance.ninjadoc.wrappers;

import com.devappliance.ninjadoc.wrappers.conf.Routes;
import com.google.inject.Singleton;
import conf.NinjaDocUiTest;
import ninja.Route;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class ApplicationContextTest extends NinjaDocUiTest {

    private ApplicationContext context;

    @Before
    public void setup() {
        context = ApplicationContext.from(injector);
    }

    @Test
    public void testGetControllerBeansWithAnnotation() {
        Map<String, Object> controllerBeansWithAnnotation = context.getControllerBeansWithAnnotation(Routes.SomeAnot.class);
        assertEquals(1, controllerBeansWithAnnotation.size());
        assertTrue(controllerBeansWithAnnotation.containsKey(Routes.TestController2.class.getSimpleName()));
    }

    public void testGetControllers() {
    }

    public void testGetBean() {
    }

    public void testFindAnnotationOnBean() {
    }

    @Test
    public void testGetType() {
        BeanType bean = context.getBean(BeanType.class);
        assertEquals("some property", bean.property);
    }

    @Test
    public void testGetRoutes() {
        List<Route> routes = context.getRoutes();
        assertFalse(routes.isEmpty());
    }

    @Override
    public void testApp() throws IOException, URISyntaxException, JSONException {
    }

    @Override
    protected String getConfigBasePackage() {
        return this.getClass().getPackage().getName();
    }

    @Singleton
    public static class BeanType {
        public String property = "some property";

    }
}
