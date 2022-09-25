package smart_room.centralized;

import smart_room.Event;

public class TestSingleBoardSimulatorExercise {

	public static void main(String[] args) throws Exception {

		SinglelBoardSimulator board = new SinglelBoardSimulator();
		board.init();
	
		board.register((Event ev) -> System.out.println("New event: " + ev));
		
		new Thread(() -> {
			while (true) {
				try {
					if (board.presenceDetected() && board.getLuminosity() < 0.5) {
						board.on();
					} else {
						board.off();
					}
					Thread.sleep(1000);
				} catch (Exception ignored) {}
			}
		}).start();
		
		while (true) {
			System.out.println("Pres Det: " + board.presenceDetected() + " - Light level: " + board.getLuminosity());
			Thread.sleep(1000);
		}
	}

}
