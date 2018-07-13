package org.stacktrace.yo.rjvm;

import org.stacktrace.yo.rjvm.ws.server.ClassLoaderServer;

public class RLoaderRunner {

    private final ClassLoaderServer myServer;

    public RLoaderRunner() {
        myServer = new ClassLoaderServer(8889);
    }

    public static void main(String... args) {
        RLoaderRunner loader = new RLoaderRunner();
    }

}
