package org.springframework.tcp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Import({TcpControllerBeanPostProcessor.class,
        TcpServerAutoStarterApplicationListener.class,
        ServerUtils.class})
public @interface EnableTcpControllers {
}
