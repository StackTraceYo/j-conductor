package org.stacktrace.yo.rjvm.loader;

import org.stacktrace.yo.rjvm.ws.client.ClassLoaderClient;

public class RemoteClassLoader extends ClassLoader {

    private final ClassLoaderClient myClient;

    public RemoteClassLoader(ClassLoaderClient myClient) {
        this.myClient = myClient;
    }
}