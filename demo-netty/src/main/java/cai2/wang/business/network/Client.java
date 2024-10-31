package cai2.wang.business.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static cai2.wang.common.constant.SystemConstant.SERVER_PORT;

public class Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", SERVER_PORT));
            socketChannel.write(StandardCharsets.UTF_8.encode("hello"));
            System.out.println("waiting...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}