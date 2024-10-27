package cai2.wang.business.network.nio;

import cai2.wang.utils.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Server {
 public static void main(String[] args) {
        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 获得服务器通道
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            // 为服务器通道绑定端口
            serverSocketChannel.bind(new InetSocketAddress(8089));
            // 将服务端连接通道设置为非阻塞模式，此种状模式，server.accept()在没有客户端连接请求建立时，返回值是null此时若没有连接，accept会返回null
            serverSocketChannel.configureBlocking(false);
            // 用户存放连接的集合
            List<SocketChannel> channelList = new ArrayList<>();
            // 循环接收连接
            while (true) {
                // configureBlocking = false （非阻塞模式）下，执行到此代码，如没有客户端连接请求建立，返回值为null
                SocketChannel socketChannel = serverSocketChannel.accept();
                // 通道不为空时才将连接放入到集合中
                if (null != socketChannel) {
                    System.out.println("client connecting...");
                    // 客户端socket通道，设置为非阻塞模式，则使用channel.read()是非阻塞，不会阻塞线程的执行
                    socketChannel.configureBlocking(false);
                    channelList.add(socketChannel);
                }
                for (SocketChannel channel : channelList) {
                    // 处理通道中的数据
                    // 在通道channel的configureBlocking = false （非阻塞模式）下，若通道中没有数据，则返回值是0，不会阻塞线程的执行
                    int read = channel.read(buffer);
                    if (read > 0) {
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                        buffer.clear();
                        System.out.println("after reading");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
