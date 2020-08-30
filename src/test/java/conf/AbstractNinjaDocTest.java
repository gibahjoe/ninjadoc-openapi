package conf;

import com.devappliance.ninjadoc.NinjaDocConfigProperties;
import com.devappliance.ninjadoc.SwaggerUiConfigProperties;
import com.devappliance.ninjadoc.config.NinjaDocConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.restassured.RestAssured;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaTestServer;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Oct, 2019
 **/

public abstract class AbstractNinjaDocTest {
    public static String className;
    public static Set<String> propertyKeys;
    protected Gson gson;
    @Inject
    protected ObjectMapper objectMapper;
    protected NinjaTestServer ninjaDocTestServer;
    @Inject
    protected NinjaDocConfig ninjaDocConfig;
    @Inject
    protected NinjaDocConfigProperties ninjaDocConfigProperties;
    @Inject
    protected SwaggerUiConfigProperties swaggerUiConfigProperties;
    protected Injector injector;


    public AbstractNinjaDocTest() {
        gson = new Gson();
    }

    protected String getConfigBasePackage() {
        String name = this.getClass().getPackage().getName();
        return name;
    }

    protected Map<String, String> getConfigsForTest() {
        Map<String, String> properties = new HashMap<>();
        TestPropertySource testPropertySource = this.getClass().getAnnotation(TestPropertySource.class);
        if (testPropertySource == null) {
            return properties;
        }
        for (String property : testPropertySource.properties()) {
            String[] keyValue = property.split("[:,=]");
            properties.put(keyValue[0], keyValue[1]);
        }
        return properties;
    }

    @Before
    public void initialize() {
        String applicationConfigBasePackage = getConfigBasePackage();
        Map<String, String> configsForTest = getConfigsForTest();
        if (configsForTest != null) {
            if (StringUtils.isNotBlank(applicationConfigBasePackage)) {
                configsForTest.put(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE, applicationConfigBasePackage);
            }
            configsForTest.forEach(System::setProperty);
            propertyKeys = configsForTest.keySet();
        }
        ninjaDocTestServer = new NinjaTestServer();
        injector = ninjaDocTestServer.getInjector();
        injector.injectMembers(this);
        RestAssured.baseURI = getServerAddress();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @After
    public void deinit() throws SQLException {
        ninjaDocTestServer.shutdown();
        propertyKeys.forEach(System::clearProperty);
        propertyKeys = null;
    }

    protected String getServerAddress() {
        return ninjaDocTestServer.getServerAddress();
    }
}
