package com.uvcity.smartpark.config;


import com.alibaba.fastjson.JSONObject;
import com.uvcity.smartpark.entity.dto.MqInfo;
import com.uvcity.smartpark.entity.po.ParkingRecord;
import com.uvcity.smartpark.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chenling
 */
@Slf4j
@Configuration
@IntegrationComponentScan
public class SpringMqttConfig {

    @Resource
    private  MqttConfig mqttConfig;
    @Resource
    private MqService mqService;

    /**
     *创建MqttPahoClientFactory，设置MQTT Broker连接属性，如果使用SSL验证，也在这里设置。
     * @return
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttConfig.getHost()});
        factory.setConnectionOptions(options);
        return factory;
    }


    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttConfig.getClientId(),
                mqttClientFactory(), mqttConfig.getIpcTopic(),mqttConfig.getRsuTopic());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    //ServiceActivator注解表明当前方法用于处理MQTT消息，inputChannel参数指定了用于接收消息信息的channel。
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String payload = message.getPayload().toString();
            String topic = String.valueOf(message.getHeaders().get("mqtt_receivedTopic"));
            // 根据topic分别进行消息处理。
            if (mqttConfig.getIpcTopic().equals(topic)) {
                log.info(topic + ": 接收IPC消息 " + payload);
                MqInfo mqInfo = JSONObject.parseObject(payload, MqInfo.class);
                ParkingRecord parkingRecord = new ParkingRecord();
                BeanUtils.copyProperties(mqInfo.getPInfo(),parkingRecord);
                mqService.saveParkRecord(parkingRecord);
                //Monitor monitor = copyProperties(matchPerson);
                //BeanUtils.copyProperties(monitor,);
                //monitorService.save(monitor);
            }  else if(mqttConfig.getRsuTopic().equals(topic)){
                log.info(topic + ": 接收RSU消息 " + payload);


            }
        };
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /*****
     * 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound() {
        // 在这里进行mqttOutboundChannel的相关设置
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler("publishClient", mqttClientFactory());
        messageHandler.setAsync(true); //如果设置成true，发送消息时将不会阻塞。
        messageHandler.setDefaultTopic("testTopic");
        return messageHandler;
    }

/*    @Component
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttGateway {
        // 定义重载方法，用于消息发送
        void sendToMqtt(String payload);
        // 指定topic进行消息发送
        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
    }*/

}
