package conf;

import com.google.inject.Injector;
import ninja.standalone.Standalone;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaTestServer;

import java.io.IOException;
import java.net.URI;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class NinjaDocTestServer extends NinjaTestServer {
    public NinjaDocTestServer() {
        super();
    }

    public NinjaDocTestServer(NinjaMode ninjaMode) {
        super(ninjaMode);
    }

    public NinjaDocTestServer(NinjaMode ninjaMode, int port) {
    }

    public NinjaDocTestServer(NinjaMode ninjaMode, Class<? extends Standalone> standaloneClass) {
        super(ninjaMode, standaloneClass);
    }

    @Override
    public NinjaTestServer ninjaMode(NinjaMode ninjaMode) {
        return super.ninjaMode(ninjaMode);
    }

    @Override
    public NinjaMode getNinjaMode() {
        return super.getNinjaMode();
    }

    @Override
    public Injector getInjector() {
        return super.getInjector();
    }

    @Override
    public String getServerUrl() {
        return super.getServerUrl();
    }

    @Override
    public String getBaseUrl() {
        return super.getBaseUrl();
    }

    @Override
    public String getServerAddress() {
        return super.getServerAddress();
    }

    @Override
    public URI getServerAddressAsUri() {
        return super.getServerAddressAsUri();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
