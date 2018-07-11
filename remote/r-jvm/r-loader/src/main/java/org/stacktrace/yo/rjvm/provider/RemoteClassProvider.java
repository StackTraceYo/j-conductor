package org.stacktrace.yo.rjvm.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.proto.rloader.RLoader;

import java.io.InputStream;
import java.net.URL;


public class RemoteClassProvider {

    private static final Logger myLogger = LoggerFactory.getLogger(RemoteClassProvider.class.getSimpleName());
    private final ClassLoaderManager myManager = new ClassLoaderManager();

    public void findClass(RLoader.LoadClass loadClass) {
        String resourceName = loadClass.getResource();
        String id = loadClass.getId();
        ClassLoader loader = myManager.get(id);
        URL resourceUrl = loader.getResource(resourceName);
        InputStream resourceStream = loader.getResourceAsStream(resourceName);

//        String id = loaderMessage.getLoadMessage().getId();

    }

}
