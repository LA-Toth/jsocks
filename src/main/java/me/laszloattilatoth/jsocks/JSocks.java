package me.laszloattilatoth.jsocks;

import me.laszloattilatoth.jsocks.proxy.SocksProxyThread;
import me.laszloattilatoth.jsocks.util.Logging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

        try (ServerSocket server = new ServerSocket(port, 10, address)) {
            while (true) {
                Socket connection = null;
                try {
                    connection = server.accept();
                    connection.setTcpNoDelay(true);
                } catch (IOException ex) {
                    Logging.logExceptionWithBacktrace(logger, ex, Level.SEVERE);
                    continue;
                }
                try {
                    SocksProxyThread proxy = new SocksProxyThread(connection, connection.getInputStream(), connection.getOutputStream());
                    proxy.start();
                } catch (Throwable e) {
                    Logging.logThrowable(logger, e, Level.INFO);
                }
            }
        } catch (
                IOException ex) {
            Logging.logExceptionWithBacktrace(logger, ex, Level.SEVERE);

            result = 1;
        }
        logger.info(() -> String.format("Ending JSocks; return_value='%d'", result));
        return result;
    }
}
