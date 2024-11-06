package cai2.wang.common.annotation;

import cai2.wang.common.config.SocketioConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>socketIo服务器开关</>
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2021/3/26 10:14
 * @since jdk1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SocketioConfig.class)
public @interface EnableSocketio {
}

