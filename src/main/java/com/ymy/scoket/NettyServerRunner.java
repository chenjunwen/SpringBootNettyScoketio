package com.ymy.scoket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author chenjunwen
 */
@Component
public class NettyServerRunner implements CommandLineRunner{
    private final SocketIOServer server;

    @Autowired
    public NettyServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
    }
}
