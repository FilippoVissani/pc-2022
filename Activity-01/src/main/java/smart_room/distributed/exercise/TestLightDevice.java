package smart_room.distributed.exercise;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import smart_room.distributed.LightDeviceSimulator;

public class TestLightDevice extends AbstractVerticle {

  private Double lastLuminosity = 0.0;
  private Boolean lastPresenceDetected = false;

  @Override
  public void start(Promise<Void> startPromise) {
    LightDeviceSimulator ld = new LightDeviceSimulator("MyLight");
    MqttClient client = MqttClient.create(vertx);
    ld.init();
    client.connect(1883, "broker.mqtt-dashboard.com", c -> {
      log("connected");
      log("subscribing...");
      client.publishHandler(s -> {
        System.out.println("There are new message in topic: " + s.topicName());
        System.out.println("Content(as string) of the message: " + Double.valueOf(s.payload().toString()));
        System.out.println("QoS: " + s.qosLevel());
        lastLuminosity = Double.valueOf(s.payload().toString());
        if (lastPresenceDetected &&lastLuminosity < 0.5){
          ld.on();
        } else {
          ld.off();
        }
      }).subscribe("LumSensorDevice", 2);
      client.publishHandler(s -> {
        System.out.println("There are new message in topic: " + s.topicName());
        System.out.println("Content(as string) of the message: " + s.payload().toString());
        System.out.println("QoS: " + s.qosLevel());
        lastPresenceDetected = Boolean.valueOf(s.payload().toString());
        if (lastPresenceDetected && lastLuminosity < 0.5){
          ld.on();
        } else {
          ld.off();
        }
      }).subscribe("PresDetectDevice", 2);
    });
  }

  private void log(String msg) {
    System.out.println("[LIGHT DEVICE] "+msg);
  }

  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    TestLightDevice agent = new TestLightDevice();
    vertx.deployVerticle(agent);
  }
}
