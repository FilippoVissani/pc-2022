package smart_room.distributed.exercise;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.Event;
import smart_room.distributed.LuminositySensorSimulator;

public class TestLumSensorDevice extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    LuminositySensorSimulator ls = new LuminositySensorSimulator("MyLightSensor");
    MqttClient client = MqttClient.create(vertx);
    ls.init();
    client.connect(1883, "broker.mqtt-dashboard.com", c -> {
      log("connected");
      ls.register((Event ev) -> {
        log("NEW EVENT " + ls.getLuminosity());
        client.publish("LumSensorDevice",
            Buffer.buffer(String.valueOf(ls.getLuminosity())),
            MqttQoS.AT_LEAST_ONCE,
            false,
            false);
      });
    });
  }

  private void log(String msg) {
    System.out.println("[LUM SENSOR] " + msg);
  }

  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    TestLumSensorDevice agent = new TestLumSensorDevice();
    vertx.deployVerticle(agent);
  }
}
