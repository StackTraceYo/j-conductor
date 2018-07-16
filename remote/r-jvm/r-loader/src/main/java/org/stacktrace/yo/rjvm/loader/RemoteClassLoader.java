package org.stacktrace.yo.rjvm.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.proto.rloader.RLoader;
import org.stacktrace.yo.rjvm.ws.client.ClassLoaderClient;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class RemoteClassLoader extends ClassLoader implements ClassLoaderClient.Listener {
    private static final Logger myLogger = LoggerFactory.getLogger(RemoteClassLoader.class.getSimpleName());
    private final ClassLoaderClient myClient;

    public RemoteClassLoader(URI serverUri) {
        this(serverUri, Thread.currentThread().getContextClassLoader());
    }

    public RemoteClassLoader(URI serverUri, ClassLoader parent) {
        super(parent);
        myClient = new ClassLoaderClient(serverUri, this);
    }

    @Override
    public Consumer<Void> onOpen() {
        return aVoid -> myLogger.debug("[RemoteClassLoader] Opened");
    }

    @Override
    public Consumer<String> onMessage(String message) {
        return null;
    }

    @Override
    public Consumer<ByteBuffer> onMessage(ByteBuffer bytes) {
        return byteBuffer -> {
            try {
                RLoader.RLoaderMessage.MessageCase messageCase = RLoader.RLoaderMessage
                        .parseFrom(bytes)
                        .getMessageCase();
                switch (messageCase) {
                    case CONNECTED:
                    case CLASSLOADED:

                }
            } catch (Exception e) {

            }
        };
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public Consumer<String> onClose(String reason) {
        return null;
    }

    @Override
    public Consumer<Exception> onError(Exception ex) {
        return null;
    }
}