import org.eclipse.paho.client.mqttv3.*;

import javax.crypto.SecretKey;
import java.util.Properties;

public class SecureSubscriber {
    public static void main(String[] args) throws Exception {
        Properties config = ConfigLoader.loadConfig();
        String broker = config.getProperty("mqtt.broker.protocol") + "://" +
                config.getProperty("mqtt.broker.host") + ":" +
                config.getProperty("mqtt.broker.port");
        String topic = config.getProperty("mqtt.topic");
        String username = config.getProperty("mqtt.broker.username");
        String password = config.getProperty("mqtt.broker.password");

        SecretKey secretKey = EncryptionUtils.loadKey();

        MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
                try {
                    System.out.println("Reconnecting...");
                    client.reconnect();
                    System.out.println("Reconnected.");
                } catch (MqttException e) {
                    System.out.println("Reconnection failed: " + e.getMessage());
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String encryptedPayload = new String(message.getPayload());
                System.out.println("Encrypted Message Received: " + encryptedPayload);

                String decryptedPayload = EncryptionUtils.decrypt(encryptedPayload, secretKey);
                System.out.println("Decrypted Message: " + decryptedPayload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        client.connect(options);
        client.subscribe(topic);

        System.out.println("Subscribed to topic: " + topic);
    }
}