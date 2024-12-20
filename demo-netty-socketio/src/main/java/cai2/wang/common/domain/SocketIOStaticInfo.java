package cai2.wang.common.domain;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一些静态变量
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/22 21:26
 * @since jdk1.8
 */
public class SocketIOStaticInfo {
    private SocketIOStaticInfo(){}
    /**用户名和websocket clientId 对应关系*/
    public static Map<String, UUID> userClientIdMap = new ConcurrentHashMap<>();
}
