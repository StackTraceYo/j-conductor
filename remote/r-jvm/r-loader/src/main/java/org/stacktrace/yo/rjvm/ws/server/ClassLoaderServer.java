package org.stacktrace.yo.rjvm.ws.server;

import com.google.protobuf.InvalidProtocolBufferException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stacktrace.yo.proto.rloader.RLoader;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClassLoaderServer extends WebSocketServer {

    private static final Logger myLogger = LoggerFactory.getLogger(ClassLoaderServer.class.getSimpleName());

    public ClassLoaderServer(InetSocketAddress address) {
        super(address);
        myLogger.debug("Server Started");
        Thread.currentThread().getContextClassLoader();
    }

    public ClassLoaderServer(Integer port) {
        this(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake clientHandshake) {
        myLogger.debug("[ClassLoader Server] Got Connection {}", clientHandshake.getResourceDescriptor());
        conn.send(
                RLoader.ConnectionRecieved.newBuilder()
                        .setReady(true)
                        .build()
                        .toByteArray()
        );
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        myLogger.debug("[ClassLoader Server] Connection Closed {}", conn.getResourceDescriptor());
    }

    @Override
    public void onMessage(WebSocket conn, String s) {
        ByteBuffer buffer = ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
        myLogger.debug("[ClassLoader Server] String Message Received -> Converting");
        onMessage(conn, buffer);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buffer) {
        myLogger.debug("[ClassLoader Server] ByteBuffer Message Received");
        try {
            RLoader.RLoaderMessage message = RLoader.RLoaderMessage
                    .parseFrom(buffer);
        } catch (InvalidProtocolBufferException e) {
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
