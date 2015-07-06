
public class SyncThread extends Thread {

	private final Object lock;

	public SyncThread(String name) {
		this.lock = new Object();
		this.setName(name);
	}

	public SyncThread(Object lock, String name) {
		this.lock = lock;
		this.setName(name);
	}

	@Override
	public void run() {
		synchronized(lock) {
			try {
				System.out.println(Thread.currentThread().getName() +
						": acquire");

				Thread.sleep(500);

				System.out.println(Thread.currentThread().getName() +
						": release");
			}
			catch(Exception ignored) {}
		}
	}
}