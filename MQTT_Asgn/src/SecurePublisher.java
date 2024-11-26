import org.eclipse.paho.client.mqttv3.*;

import javax.crypto.SecretKey;
import java.util.Properties;

public class SecurePublisher {
    public static void main(String[] args) throws Exception {
        Properties config = ConfigLoader.loadConfig();
        String broker = config.getProperty("mqtt.broker.protocol") + "://" +
                config.getProperty("mqtt.broker.host") + ":" +
                config.getProperty("mqtt.broker.port");
        String topic = config.getProperty("mqtt.topic");
        String username = config.getProperty("mqtt.broker.username");
        String password = config.getProperty("mqtt.broker.password");
        String payload = config.getProperty("mqtt.broker.payload");

        SecretKey secretKey = EncryptionUtils.generateKey();
        EncryptionUtils.saveKey(secretKey);

//        String payload = "Hello Secure World!";
        String encryptedPayload = EncryptionUtils.encrypt(payload, secretKey);

        MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        client.connect(options);

        MqttMessage message = new MqttMessage(encryptedPayload.getBytes());
        client.publish(topic, message);

        System.out.println("Message published: " + encryptedPayload + " at topic " + topic);

        Thread.sleep(5000);

        client.publish(topic, message);

        client.disconnect();
        System.out.println("Publisher disconnected.");
    }
}