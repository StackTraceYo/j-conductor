package org.stacktrace.yo.rjvm;

import org.stacktrace.yo.rjvm.ws.server.ClassLoaderServer;

public class RLoader {

    private final ClassLoaderServer myServer;

    public RLoader() {
        myServer = new ClassLoaderServer(8889);
    }

    public static void main(String... args) {
        RLoader loader = new RLoader();
    }

}
