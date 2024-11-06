package cai2.wang.common.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Optional;

/**
 * socket.io
 *
 * @author wangpeixu
 * @vesion 1.0
 * @date 2021/3/25 8:51
 * @since jdk1.8
 */
//@Configuration
@Data
public class SocketioConfig {
//    @Value("${socket.io.port:8974}")
    private Integer socketIoPort = 9090;
    @Value("${socket.io.namespaces}")
    private String[] namespaces;

/**
 * 创建并配置SocketIOServer实例
 * 该方法首先创建一个SocketIO服务器配置实例，然后设置各项配置参数，
 * 如端口、工作线程数、跨域设置等最后，根据配置创建一个SocketIOServer实例，
 * 并根据需要添加命名空间返回配置好的SocketIOServer实例
 */
@Bean
public SocketIOServer socketIOServer() {
    // 创建SocketIO服务器配置实例
    com.corundumstudio.socketio.Configuration config =
            new com.corundumstudio.socketio.Configuration();
    // 设置跨域设置为null，表示不允许跨域请求
    config.setOrigin(null);   // 注意如果开放跨域设置，需要设置为null而不是"*"
    // 设置服务器监听端口
    config.setPort(socketIoPort);
    // 设置Socket配置
    config.setSocketConfig(new SocketConfig());
    // 设置工作线程数，以处理并发连接
    config.setWorkerThreads(100);
    // 设置授权监听器，此处简化处理，直接返回true表示所有握手请求都通过
    config.setAuthorizationListener(handshakeData -> true);
    // 允许最大帧长度
    config.setMaxFramePayloadLength(1024 * 1024);
    // 允许下最大内容
    config.setMaxHttpContentLength(1024 * 1024);
    // 创建SocketIOServer实例
    final SocketIOServer server = new SocketIOServer(config);
    // 如果有命名空间配置，则添加到服务器中
    Optional.ofNullable(namespaces).ifPresent(nss ->
            Arrays.stream(nss).forEach(server::addNamespace));
    // 此处不启动服务器，留到应用启动时统一启动
//        server.start();
    // 返回配置好的SocketIOServer实例
    return server;

}


    /**
     * 注入OnConnect，OnDisconnect，OnEvent注解。 不写的话Spring无法扫描OnConnect，OnDisconnect等注解
     * */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer){
        return new SpringAnnotationScanner(socketIOServer);
    }
}

