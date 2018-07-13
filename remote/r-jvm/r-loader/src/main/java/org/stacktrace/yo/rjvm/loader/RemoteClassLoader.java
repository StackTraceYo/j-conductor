package org.stacktrace.yo.rjvm.loader;

import org.stacktrace.yo.rjvm.ws.client.ClassLoaderClient;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class RemoteClassLoader extends ClassLoader {

    private final ClassLoaderClient myClient;

    public RemoteClassLoader(URI serverUri) {
        this(serverUri, Thread.currentThread().getContextClassLoader());
    }

    public RemoteClassLoader(URI serverUri, ClassLoader parent) {
        super(parent);
        myClient = new ClassLoaderClient(serverUri);
    }

    public static final class ClientListener implements ClassLoaderClient.Listener {

        @Override
        public Consumer<Void> onOpen() {
            return null;
        }

        @Override
        public Consumer<String> onMessage(String message) {
            return null;
        }

        @Override
        public Consumer<ByteBuffer> onMessage(ByteBuffer bytes) {
            return null;
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

}