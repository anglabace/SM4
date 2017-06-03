/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.amqp.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Message Properties for an AMQP message.
 *
 * @author Mark Fisher
 * @author Mark Pollack
 * @author Gary Russell
 */
public class MessageProperties implements Serializable {

    public static final String CONTENT_TYPE_BYTES = "application/octet-stream";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_JSON_ALT = "text/x-json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String SPRING_BATCH_FORMAT = "springBatchFormat";
    public static final String BATCH_FORMAT_LENGTH_HEADER4 = "lengthHeader4";
    public static final String SPRING_AUTO_DECOMPRESS = "springAutoDecompress";
    static final String DEFAULT_CONTENT_TYPE = CONTENT_TYPE_BYTES;
    static final MessageDeliveryMode DEFAULT_DELIVERY_MODE = MessageDeliveryMode.PERSISTENT;
    static final Integer DEFAULT_PRIORITY = Integer.valueOf(0);
    private static final long serialVersionUID = 1619000546531112290L;
    private final Map<String, Object> headers = new HashMap<String, Object>();

    private volatile Date timestamp;

    private volatile String messageId;

    private volatile String userId;

    private volatile String appId;

    private volatile String clusterId;

    private volatile String type;

    private volatile byte[] correlationId;

    private volatile String replyTo;

    private volatile String contentType = DEFAULT_CONTENT_TYPE;

    private volatile String contentEncoding;

    private volatile long contentLength;

    private volatile boolean contentLengthSet;

    private volatile MessageDeliveryMode deliveryMode = DEFAULT_DELIVERY_MODE;

    private volatile String expiration;

    private volatile Integer priority = DEFAULT_PRIORITY;

    private volatile Boolean redelivered;

    private volatile String receivedExchange;

    private volatile String receivedRoutingKey;

    private volatile long deliveryTag;

    private volatile boolean deliveryTagSet;

    private volatile Integer messageCount;

    // Not included in hashCode()

    private volatile String consumerTag;

    private volatile String consumerQueue;

    public void setHeader(String key, Object value) {
        this.headers.put(key, value);
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    // NOTE qpid java timestamp is long, presumably can convert to Date.
    public Date getTimestamp() {
        return this.timestamp;//NOSONAR
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;//NOSONAR
    }

    public String getMessageId() {
        return this.messageId;
    }

    // NOTE Not forward compatible with qpid 1.0 .NET
    // qpid 0.8 .NET/Java: is a string
    // qpid 1.0 .NET: MessageId property on class MessageProperties and is UUID
    // There is an 'ID' stored IMessage class and is an int.
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // NOTE Note forward compatible with qpid 1.0 .NET
    // qpid 0.8 .NET/java: is a string
    // qpid 1.0 .NET: getUserId is byte[]
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getClusterId() {
        return this.clusterId;
    }

    // NOTE not forward compatible with qpid 1.0 .NET
    // qpid 0.8 .NET/Java: is a string
    // qpid 1.0 .NET: is not present
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    // NOTE stuctureType is int in qpid
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getCorrelationId() {
        return this.correlationId;//NOSONAR
    }

    public void setCorrelationId(byte[] correlationId) {
        this.correlationId = correlationId;//NOSONAR
    }

    public String getReplyTo() {
        return this.replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Address getReplyToAddress() {
        return (this.replyTo != null) ? new Address(this.replyTo) : null;
    }

    public void setReplyToAddress(Address replyTo) {
        this.replyTo = (replyTo != null) ? replyTo.toString() : null;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
        this.contentLengthSet = true;
    }

    protected final boolean isContentLengthSet() {
        return this.contentLengthSet;
    }

    public MessageDeliveryMode getDeliveryMode() {
        return this.deliveryMode;
    }

    public void setDeliveryMode(MessageDeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    // NOTE qpid Java broker qpid 0.8/1.0 .NET: is a long.
    // 0.8 Spec has: expiration (shortstr)
    public String getExpiration() {
        return this.expiration;
    }

    // why not a Date or long?
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getReceivedExchange() {
        return this.receivedExchange;
    }

    public void setReceivedExchange(String receivedExchange) {
        this.receivedExchange = receivedExchange;
    }

    public String getReceivedRoutingKey() {
        return this.receivedRoutingKey;
    }

    public void setReceivedRoutingKey(String receivedRoutingKey) {
        this.receivedRoutingKey = receivedRoutingKey;
    }

    public Boolean isRedelivered() {
        return this.redelivered;
    }

    /*
     * Additional accessor because is* is not standard for type Boolean
     */
    public Boolean getRedelivered() {
        return this.redelivered;
    }

    public void setRedelivered(Boolean redelivered) {
        this.redelivered = redelivered;
    }

    public long getDeliveryTag() {
        return this.deliveryTag;
    }

    public void setDeliveryTag(long deliveryTag) {
        this.deliveryTag = deliveryTag;
        this.deliveryTagSet = true;
    }

    protected final boolean isDeliveryTagSet() {
        return this.deliveryTagSet;
    }

    public Integer getMessageCount() {
        return this.messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public String getConsumerTag() {
        return this.consumerTag;
    }

    public void setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
    }

    public String getConsumerQueue() {
        return this.consumerQueue;
    }

    public void setConsumerQueue(String consumerQueue) {
        this.consumerQueue = consumerQueue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appId == null) ? 0 : appId.hashCode());
        result = prime * result + ((clusterId == null) ? 0 : clusterId.hashCode());
        result = prime * result + ((contentEncoding == null) ? 0 : contentEncoding.hashCode());
        result = prime * result + (int) (contentLength ^ (contentLength >>> 32));
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + Arrays.hashCode(correlationId);
        result = prime * result + ((deliveryMode == null) ? 0 : deliveryMode.hashCode());
        result = prime * result + (int) (deliveryTag ^ (deliveryTag >>> 32));
        result = prime * result + ((expiration == null) ? 0 : expiration.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((messageCount == null) ? 0 : messageCount.hashCode());
        result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((receivedExchange == null) ? 0 : receivedExchange.hashCode());
        result = prime * result + ((receivedRoutingKey == null) ? 0 : receivedRoutingKey.hashCode());
        result = prime * result + ((redelivered == null) ? 0 : redelivered.hashCode());
        result = prime * result + ((replyTo == null) ? 0 : replyTo.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MessageProperties other = (MessageProperties) obj;
        if (appId == null) {
            if (other.appId != null) {
                return false;
            }
        } else if (!appId.equals(other.appId)) {
            return false;
        }
        if (clusterId == null) {
            if (other.clusterId != null) {
                return false;
            }
        } else if (!clusterId.equals(other.clusterId)) {
            return false;
        }
        if (contentEncoding == null) {
            if (other.contentEncoding != null) {
                return false;
            }
        } else if (!contentEncoding.equals(other.contentEncoding)) {
            return false;
        }
        if (contentLength != other.contentLength) {
            return false;
        }
        if (contentType == null) {
            if (other.contentType != null) {
                return false;
            }
        } else if (!contentType.equals(other.contentType)) {
            return false;
        }
        if (!Arrays.equals(correlationId, other.correlationId)) {
            return false;
        }
        if (deliveryMode != other.deliveryMode) {
            return false;
        }
        if (deliveryTag != other.deliveryTag) {
            return false;
        }
        if (expiration == null) {
            if (other.expiration != null) {
                return false;
            }
        } else if (!expiration.equals(other.expiration)) {
            return false;
        }
        if (headers == null) {
            if (other.headers != null) {
                return false;
            }
        } else if (!headers.equals(other.headers)) {
            return false;
        }
        if (messageCount == null) {
            if (other.messageCount != null) {
                return false;
            }
        } else if (!messageCount.equals(other.messageCount)) {
            return false;
        }
        if (messageId == null) {
            if (other.messageId != null) {
                return false;
            }
        } else if (!messageId.equals(other.messageId)) {
            return false;
        }
        if (priority == null) {
            if (other.priority != null) {
                return false;
            }
        } else if (!priority.equals(other.priority)) {
            return false;
        }
        if (receivedExchange == null) {
            if (other.receivedExchange != null) {
                return false;
            }
        } else if (!receivedExchange.equals(other.receivedExchange)) {
            return false;
        }
        if (receivedRoutingKey == null) {
            if (other.receivedRoutingKey != null) {
                return false;
            }
        } else if (!receivedRoutingKey.equals(other.receivedRoutingKey)) {
            return false;
        }
        if (redelivered == null) {
            if (other.redelivered != null) {
                return false;
            }
        } else if (!redelivered.equals(other.redelivered)) {
            return false;
        }
        if (replyTo == null) {
            if (other.replyTo != null) {
                return false;
            }
        } else if (!replyTo.equals(other.replyTo)) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else if (!userId.equals(other.userId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MessageProperties [headers=" + headers + ", timestamp=" + timestamp + ", messageId=" + messageId + ", userId=" + userId + ", appId=" + appId + ", clusterId=" + clusterId + ", type=" + type + ", correlationId=" + Arrays.toString(correlationId) + ", replyTo=" + replyTo + ", contentType=" + contentType + ", contentEncoding=" + contentEncoding + ", contentLength=" + contentLength + ", deliveryMode=" + deliveryMode + ", expiration=" + expiration + ", priority=" + priority + ", redelivered=" + redelivered + ", receivedExchange=" + receivedExchange + ", receivedRoutingKey=" + receivedRoutingKey + ", deliveryTag=" + deliveryTag + ", messageCount=" + messageCount + "]";
    }

}
