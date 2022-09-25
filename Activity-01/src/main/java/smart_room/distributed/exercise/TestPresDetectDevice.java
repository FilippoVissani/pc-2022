package smart_room.distributed.exercise;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.Event;
import smart_room.distributed.PresDetectSensorSimulator;

public class TestPresDetectDevice extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    PresDetectSensorSimulator pd = new PresDetectSensorSimulator("MyPIR");
    MqttClient client = MqttClient.create(vertx);
    pd.init();
    client.connect(1883, "broker.mqtt-dashboard.com", c -> {
      log("connected");
      pd.register((Event ev) -> {
        log("NEW EVENT " + pd.presenceDetected());
        client.publish("PresDetectDevice",
            Buffer.buffer(String.valueOf(pd.presenceDetected())),
            MqttQoS.AT_LEAST_ONCE,
            false,
            false);
      });
    });
  }

  private void log(String msg) {
    System.out.println("[PRES SENSOR] " + msg);
  }

  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    TestPresDetectDevice agent = new TestPresDetectDevice();
    vertx.deployVerticle(agent);
  }
}
