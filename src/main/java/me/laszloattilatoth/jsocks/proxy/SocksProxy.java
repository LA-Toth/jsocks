package me.laszloattilatoth.jsocks.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public abstract class SocksProxy {
    protected final InputStream inputStream;
    protected final OutputStream outputStream;
    protected final Socket socket;
    protected final int threadId;
    protected final String name;
    protected Logger logger;

    SocksProxy(Socket s, InputStream is, OutputStream os, Logger logger, String name, int threadId) {
        this.socket = s;
        this.inputStream = is;
        this.outputStream = os;
        this.logger = logger;
        this.threadId = threadId;
        this.name = name;
    }

    public static SocksProxy create(int version, Socket s, InputStream is, OutputStream os, Logger logger, String name, int threadId) {
        if (version == 4)
            return new SocksV4Proxy(s, is, os, logger, name, threadId);
        return new SocksV5Proxy(s, is, os, logger, name, threadId);
    }

    public abstract void run() throws IOException;
}
