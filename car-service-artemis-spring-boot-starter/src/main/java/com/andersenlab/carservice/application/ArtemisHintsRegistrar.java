package com.andersenlab.carservice.application;

import jakarta.jms.*;
import org.apache.activemq.artemis.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.core.client.ActiveMQClientLogger_$logger;
import org.apache.activemq.artemis.core.client.ActiveMQClientMessageBundle;
import org.apache.activemq.artemis.core.client.ActiveMQClientMessageBundle_$bundle;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.logs.ActiveMQUtilLogger_$logger;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.jms.connection.SessionProxy;

import java.util.Calendar;
import java.util.UUID;

final class ArtemisHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection()
                .registerType(
                        ActiveMQConnectionFactory.class,
                        builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
                )
                .registerType(
                        Calendar[].class,
                        builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
                )
                .registerType(
                        ActiveMQUtilLogger_$logger.class,
                        builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
                )
                .registerType(
                        ActiveMQClientLogger_$logger.class,
                        builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
                )
                .registerType(
                        ActiveMQClientMessageBundle_$bundle.class,
                        builder ->
                                builder.withMembers(
                                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                        MemberCategory.PUBLIC_FIELDS
                                )
                )
                .registerType(
                        ActiveMQClientMessageBundle.class,
                        builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS)
                )
                .registerType(
                        RoundRobinConnectionLoadBalancingPolicy.class,
                        builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
                )
                .registerType(
                        NettyConnectorFactory.class,
                        builder ->
                                builder.withMembers(
                                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                        MemberCategory.INVOKE_PUBLIC_METHODS
                                )
                );
        hints.resources().registerPattern("activemq-version.properties");
        hints.proxies()
                .registerJdkProxy(
                        Connection.class,
                        QueueConnection.class,
                        TopicConnection.class
                )
                .registerJdkProxy(
                        SessionProxy.class,
                        QueueSession.class,
                        TopicSession.class
                );
        hints.serialization().registerType(UUID.class);
    }
}
