package cai2.wang.common;

import cai2.wang.common.utils.SpringContextHolder;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2021/3/25 10:07
 * @since jdk1.8
 */
@Component
public class ServerRunner implements CommandLineRunner {
    @Autowired(required = false)
    private SocketIOServer socketIOServer;

    @Value("${socket.io.namespaces}")
    private String[] namespaces;

    @Override
    public void run(String... args) throws Exception {
        if (socketIOServer != null) {
            /*Optional.ofNullable(SpringService.getBean("messageEventHandler"))
                    .ifPresent(handler -> socketIOServer.getNamespace("/").addListeners(handler));*/


            Optional.ofNullable(namespaces).ifPresent(nss ->
                    Arrays.stream(nss).forEach(ns -> {
                        //获取命名空间
                        SocketIONamespace socketIONamespace = socketIOServer.getNamespace(ns);
                        //获取期待的类名
                        String className = ns.substring(1) + "MessageEventHandler";
                        try {
                            Object bean = SpringContextHolder.getBean(className);
                            Optional.ofNullable(bean).ifPresent(socketIONamespace::addListeners);
                        } catch (Exception e) {

                        }

                    }));
//            socketIOServer.getNamespace("/chat").addListeners(messageEventHandler);
            socketIOServer.start();
        }
    }
}

