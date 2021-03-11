package me.laszloattilatoth.jsocks.proxy;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class SocksV5Proxy extends SocksProxy {

    SocksV5Proxy(SocketChannel s, Logger logger, String name, int threadId) {
        super(s, logger, name, threadId);
    }

    @Override
    public void run() throws IOException {
        System.out.println("Running SOCKS v5");
    }

    @Override
    protected void sendConnectionSuccessMsg() throws IOException {
    }

    @Override
    protected void sendConnectionFailureMsg() throws IOException {
    }
}
