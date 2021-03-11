package me.laszloattilatoth.jsocks.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class SocksV4Proxy extends SocksProxy {
    private final byte[] portBytes = new byte[2];
    private final byte[] addrBytes = new byte[4];

    SocksV4Proxy(SocketChannel s, Logger logger, String name, int threadId) {
        super(s, logger, name, threadId);
    }

    @Override
    public void run() throws IOException {
        InputStream inputStream = socketChannel.socket().getInputStream();
        int cmd = inputStream.read();
        if (cmd != 1)
            return;

        if (inputStream.read(portBytes) <= 0 || inputStream.read(addrBytes) <= 0 || inputStream.read() < 0)
            return;

        short port = ByteBuffer.wrap(portBytes).getShort();
        InetAddress address = InetAddress.getByAddress(addrBytes);

        connectAndTransfer(address, port);
    }

    @Override
    protected void sendConnectionSuccessMsg() throws IOException {
        socketChannel.socket().getOutputStream().write(new byte[]{0, 0x5A}, 0, 2);
        socketChannel.socket().getOutputStream().write(portBytes);
        socketChannel.socket().getOutputStream().write(addrBytes);
    }

    @Override
    protected void sendConnectionFailureMsg() throws IOException {
        socketChannel.socket().getOutputStream().write(new byte[]{0, 0x5B}, 0, 2);
        socketChannel.socket().getOutputStream().write(portBytes);
        socketChannel.socket().getOutputStream().write(addrBytes);
    }
}
