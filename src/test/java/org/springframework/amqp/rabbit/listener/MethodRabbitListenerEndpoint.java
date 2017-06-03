/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.amqp.rabbit.listener;

import org.springframework.amqp.core.Address;
import org.springframework.amqp.rabbit.listener.adapter.HandlerAdapter;
import org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A {@link RabbitListenerEndpoint} providing the method to invoke to process
 * an incoming message for this endpoint.
 *
 * @author Stephane Nicoll
 * @author Artem Bilan
 * @since 1.4
 */
public class MethodRabbitListenerEndpoint extends AbstractRabbitListenerEndpoint {

    private Object bean;

    private Method method;

    private MessageHandlerMethodFactory messageHandlerMethodFactory;

    public Object getBean() {
        return this.bean;
    }

    /**
     * Set the object instance that should manage this endpoint.
     *
     * @param bean the target bean instance.
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return this.method;
    }

    /**
     * Set the method to invoke to process a message managed by this endpoint.
     *
     * @param method the target method for the {@link #bean}.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * @return the messageHandlerMethodFactory
     */
    protected MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        return messageHandlerMethodFactory;
    }

    /**
     * Set the {@link MessageHandlerMethodFactory} to use to build the
     * {@link InvocableHandlerMethod} responsible to manage the invocation
     * of this endpoint.
     *
     * @param messageHandlerMethodFactory the {@link MessageHandlerMethodFactory} instance.
     */
    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
    }

    @Override
    protected MessagingMessageListenerAdapter createMessageListener(MessageListenerContainer container) {
        Assert.state(this.messageHandlerMethodFactory != null, "Could not create message listener - MessageHandlerMethodFactory not set");
        MessagingMessageListenerAdapter messageListener = createMessageListenerInstance();
        messageListener.setHandlerMethod(configureListenerAdapter(messageListener));
        Address replyToAddress = getDefaultReplyToAddress();
        if (replyToAddress != null) {
            messageListener.setResponseExchange(replyToAddress.getExchangeName());
            messageListener.setResponseRoutingKey(replyToAddress.getRoutingKey());
        }
        MessageConverter messageConverter = container.getMessageConverter();
        if (messageConverter != null) {
            messageListener.setMessageConverter(messageConverter);
        }
        return messageListener;
    }

    /**
     * Create a {@link HandlerAdapter} for this listener adapter.
     *
     * @param messageListener the listener adapter.
     * @return the handler adapter.
     */
    protected HandlerAdapter configureListenerAdapter(MessagingMessageListenerAdapter messageListener) {
        InvocableHandlerMethod invocableHandlerMethod = this.messageHandlerMethodFactory.createInvocableHandlerMethod(getBean(), getMethod());
        return new HandlerAdapter(invocableHandlerMethod);
    }

    /**
     * Create an empty {@link MessagingMessageListenerAdapter} instance.
     *
     * @return the {@link MessagingMessageListenerAdapter} instance.
     */
    protected MessagingMessageListenerAdapter createMessageListenerInstance() {
        return new MessagingMessageListenerAdapter();
    }

    private Address getDefaultReplyToAddress() {
        SendTo ann = AnnotationUtils.getAnnotation(getMethod(), SendTo.class);
        if (ann != null) {
            String[] destinations = ann.value();
            if (destinations.length > 1) {
                throw new IllegalStateException("Invalid @" + SendTo.class.getSimpleName() + " annotation on '" + getMethod() + "' one destination must be set (got " + Arrays.toString(destinations) + ")");
            }
            return destinations.length == 1 ? new Address(destinations[0]) : new Address(null);
        }
        return null;
    }

    @Override
    protected StringBuilder getEndpointDescription() {
        return super.getEndpointDescription().append(" | bean='").append(this.bean).append("'").append(" | method='").append(this.method).append("'");
    }

}
