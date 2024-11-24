import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

public class MqttPublisher {

    public static void main(String[] args) {
        try {
            // Load properties from the file
            Properties properties = ConfigLoader.loadConfig();
            String broker = properties.getProperty("mqtt.broker.host");
            int port = Integer.parseInt(properties.getProperty("mqtt.broker.port"));
            String protocol = properties.getProperty("mqtt.broker.protocol");
            String username = properties.getProperty("mqtt.broker.username");
            String password = properties.getProperty("mqtt.broker.password");
            int keepAliveInterval = Integer.parseInt(properties.getProperty("mqtt.broker.keepalive"));
            String certPath = properties.getProperty("ssl.cert.path");
            String keyPath = properties.getProperty("ssl.key.path");
            String truststorePath = properties.getProperty("ssl.truststore.path");
            String truststorePassword = properties.getProperty("ssl.truststore.password");

            // Set up SSL Context if SSL is enabled
            SSLContext sslContext = null;
            if (Boolean.parseBoolean(properties.getProperty("ssl.enabled"))) {
                sslContext = createSSLContext(truststorePath, truststorePassword);
            }

            // Create connection options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setKeepAliveInterval(keepAliveInterval);

            if (sslContext != null) {
                options.setSocketFactory(sslContext.getSocketFactory());
            }

            // Create MQTT client with persistence
            MqttClient client = new MqttClient(protocol + "://" + broker + ":" + port, MqttClient.generateClientId(), new MemoryPersistence());
            client.connect(options);

            // Publish a message
            String topic = properties.getProperty("mqtt.topic");
            String message = "Hello, MQTT with SSL!";
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage);

            System.out.println("Message published to topic: " + topic);

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SSLContext createSSLContext(String truststorePath, String truststorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream trustStream = new FileInputStream(truststorePath)) {
            trustStore.load(trustStream, truststorePassword.toCharArray());
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
        return sslContext;
    }
}