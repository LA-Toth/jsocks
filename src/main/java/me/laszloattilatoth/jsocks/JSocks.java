package me.laszloattilatoth.jsocks;

import me.laszloattilatoth.jsocks.proxy.SocksProxyThread;
import me.laszloattilatoth.jsocks.util.Logging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSocks {
    private final InetAddress address;
    private final int port;
    private final Logger logger;

    JSocks(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.logger = Logger.getGlobal();
    }

    int run() {
        logger.info(() -> "Starting JSocks;");
        int result;

        try {
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(address, port), 10);
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                if (selector.select() <= 0)
                    continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = ssc.accept();

                        try {
                            SocksProxyThread proxy = new SocksProxyThread(socketChannel);
                            proxy.start();
                        } catch (Throwable e) {
                            Logging.logThrowable(logger, e, Level.INFO);
                        }
                    }
                }
                keys.clear();
            }
        } catch (IOException ex) {
            Logging.logExceptionWithBacktrace(logger, ex, Level.SEVERE);

            result = 1;
        }
        logger.info(() -> String.format("Ending JSocks; return_value='%d'", result));
        return result;
    }
}
