import org.eclipse.paho.client.mqttv3.*;

import javax.crypto.SecretKey;

public class SecurePublisher {
    public static void main(String[] args) throws Exception {
        String broker = "ssl://localhost:8883";
        String topic = "test/secure";
        String username = "user";
        String password = "!Aa123456";

        // Generate an encryption key
        SecretKey secretKey = EncryptionUtils.generateKey();

        // Encrypt the payload
        String payload = "Hello Secure World!";
        String encryptedPayload = EncryptionUtils.encrypt(payload, secretKey);

        // Initialize the MQTT client
        MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        client.connect(options);

        // Publish the encrypted message
        MqttMessage message = new MqttMessage(encryptedPayload.getBytes());
        client.publish(topic, message);

        System.out.println("Message published: " + encryptedPayload);

        client.disconnect();
        System.out.println("Disconnected.");
    }
}
