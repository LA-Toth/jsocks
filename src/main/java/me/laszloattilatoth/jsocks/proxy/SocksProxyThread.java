package me.laszloattilatoth.jsocks.proxy;

import me.laszloattilatoth.jsocks.util.Logging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocksProxyThread extends Thread {
    private static final AtomicInteger previousThreadId = new AtomicInteger();
    protected final InputStream inputStream;
    protected final OutputStream outputStream;
    protected final Socket socket;
    protected final int socksThreadId;
    protected Logger logger;

    public SocksProxyThread(Socket s, InputStream is, OutputStream os) {
        this.socket = s;
        this.inputStream = is;
        this.outputStream = os;
        this.socksThreadId = previousThreadId.incrementAndGet();

        this.setName("SocksProxy:" + this.socksThreadId);
        this.logger = createLogger();
    }

    private Logger createLogger() {
        return Logger.getLogger(getName());
    }

    @Override
    public void run() {
        logger.info(String.format("Starting proxy instance; client_address='%s:%d', client_local='%s:%d'",
                socket.getInetAddress().getHostAddress(), socket.getPort(),
                socket.getLocalAddress().getHostAddress(), socket.getLocalPort()));
        try {
            this.main();
            this.socket.close();
        } catch (IOException e) {
            logger.severe(String.format("IOException occurred; message='%s'", e.getMessage()));
            Logging.logExceptionWithBacktrace(logger, e, Level.INFO);
        } finally {
            logger.info("Ending proxy instance;");
        }
    }

    private void main() throws IOException {
        int version = this.inputStream.read();
        System.out.println(version);

        SocksProxy p = null;
        if (version == -1) {
            return;
        } else if (version == 4 || version == 5) {
            p = SocksProxy.create(version, socket, inputStream, outputStream, logger, getName(), socksThreadId);
        } else {
        }

        if (p != null) {
            p.run();
        }
    }
}
