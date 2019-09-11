package com.opencore.kafka;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Properties adminProps = new Properties();
        adminProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

        AdminClient adminClient = AdminClient.create(adminProps);

        try {
            Collection<Node> brokerNodes = adminClient.describeCluster().nodes().get();

            // Create a new ConfigObject for every broker
            Collection<ConfigResource> brokerList = brokerNodes.stream()
                .map(e -> new ConfigResource(ConfigResource.Type.BROKER, String.valueOf(e.id())))
                .collect(Collectors.toSet());

            Map<ConfigResource, Config> brokerConfigMap = adminClient.describeConfigs(brokerList).all().get();

            for (ConfigResource broker : brokerConfigMap.keySet()) {
                System.out.println("Broker " + broker.name() + ": " + brokerConfigMap.get(broker).get("message.max.bytes").value());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
