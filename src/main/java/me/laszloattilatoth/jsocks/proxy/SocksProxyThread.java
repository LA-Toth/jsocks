package me.laszloattilatoth.jsocks.proxy;

import me.laszloattilatoth.jsocks.util.Logging;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocksProxyThread extends Thread {
    private static final AtomicInteger previousThreadId = new AtomicInteger();
    protected final int socksThreadId;
    protected SocketChannel socketChannel;
    protected Logger logger;

    public SocksProxyThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.socksThreadId = previousThreadId.incrementAndGet();

        this.setName("SocksProxy:" + this.socksThreadId);
        this.logger = createLogger();
    }

    private Logger createLogger() {
        return Logger.getLogger(getName());
    }

    @Override
    public void run() {
        try {
            logger.info(String.format("Starting proxy instance; client_address='%s', client_local='%s'",
                    socketChannel.getRemoteAddress(), socketChannel.getLocalAddress()));
            this.main();
            this.socketChannel.close();
        } catch (IOException e) {
            logger.severe(String.format("IOException occurred; message='%s'", e.getMessage()));
            Logging.logExceptionWithBacktrace(logger, e, Level.INFO);
        } finally {
            logger.info("Ending proxy instance;");
        }
    }

    private void main() throws IOException {
        socketChannel.configureBlocking(true);
        int version = socketChannel.socket().getInputStream().read();
        System.out.println(version);

        SocksProxy p = null;
        if (version == -1) {
            return;
        } else if (version == 4 || version == 5) {
            p = SocksProxy.create(version, socketChannel, logger, getName(), socksThreadId);
        } else {
        }

        if (p != null) {
            p.run();
        }
    }
}
