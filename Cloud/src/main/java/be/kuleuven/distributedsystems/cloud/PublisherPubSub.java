package be.kuleuven.distributedsystems.cloud;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;

public class PublisherPubSub {
    private String project_id = "dscloud";
    private String topic_id = "bookingAQuote";

    PublisherPubSub(){
        String hostport = System.getenv("PUBSUB_EMULATOR_HOST");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
        try {
            TransportChannelProvider channelProvider =
                    FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
            CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

            // Set the channel and credentials provider when creating a `TopicAdminClient`.
            // Similarly for SubscriptionAdminClient
            TopicAdminClient topicClient =
                    TopicAdminClient.create(
                            TopicAdminSettings.newBuilder()
                                    .setTransportChannelProvider(channelProvider)
                                    .setCredentialsProvider(credentialsProvider)
                                    .build());

            TopicName topicName = TopicName.of(project_id, topic_id);
            // Set the channel and credentials provider when creating a `Publisher`.
            // Similarly for Subscriber
            Publisher publisher =
                    Publisher.newBuilder(topicName)
                            .setChannelProvider(channelProvider)
                            .setCredentialsProvider(credentialsProvider)
                            .build();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }

    void publishMessage(String topicId,String dataString) throws IOException {
        TopicName topicname = TopicName.of(project_id,topicId);
        Publisher publisher = Publisher.newBuilder(topicname).build();
        ByteString data = ByteString.copyFromUtf8(dataString);
        String key = "mamamia"; String value = "E4"; // to choose
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(data)
                .putAttributes(key,value)
                .build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
    }

    //How to find urlOfSubscriber (Worker Object)
    void createPushNotification(String urlOfSubscriber,String topicName,String subscriptionName) throws IOException {
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create();
        PushConfig pushConfig = PushConfig.newBuilder()
                .setPushEndpoint(urlOfSubscriber)
                .build();
        subscriptionAdminClient.createSubscription(subscriptionName, topicName, pushConfig, 60);
    }
}
