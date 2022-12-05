package com.coditory.sherlock;

import java.io.IOException;
import java.net.ServerSocket;

final class Ports {
    public static int nextAvailablePort() {
        try {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not get free port", exception);
        }
    }
}
