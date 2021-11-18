package be.kuleuven.distributedsystems.cloud;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;

public class PublisherPubSub {
    PublisherPubSub(){

    }

    void publishMessage(String projectId,String topicId,ByteString data) throws IOException {
        TopicName topicname = TopicName.of(projectId,topicId);
        Publisher publisher = Publisher.newBuilder(topicname).build();
        //ByteString data = new ByteString();
        String key = "mamamia"; String value = "E4";
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(data)
                .putAttributes(key,value)
                .build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
    }

    void createPushNotification(String urlOfSubscriber,String topicName,String subscriptionName) throws IOException {
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();
        PushConfig pushConfig = PushConfig.newBuilder()
                .setPushEndpoint(urlOfSubscriber)
                .build();
        subscriptionAdminClient.createSubscription(subscriptionName, topicName, pushConfig, 60);
    }



}
