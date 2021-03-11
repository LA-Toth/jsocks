package me.laszloattilatoth.jsocks.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class SocksV5Proxy extends SocksProxy {

    SocksV5Proxy(Socket s, InputStream is, OutputStream os, Logger logger, String name, int threadId) {
        super(s, is, os, logger, name, threadId);
    }

    @Override
    public void run() throws IOException {
        System.out.println("Running SOCKS v5");
    }
}
