
public class SyncQuiz {

	private Object lock1;
	private final Object lock2;
	private static Object lock3 = new Object();
	private static final Object lock4 = new Object();

	private final String name;

	public SyncQuiz(String name) {
		this.lock1 = new Object();
		this.lock2 = new Object();
		this.name  = name;
	}

	private void quiz01() throws InterruptedException {
		Thread t1 = new SyncThread(lock1, name + "1");
		Thread t2 = new SyncThread(lock1, name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private void quiz02() throws InterruptedException {
		Thread t1 = new SyncThread(lock2, name + "1");
		Thread t2 = new SyncThread(lock2, name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private void quiz03() throws InterruptedException {
		Thread t1 = new SyncThread(lock3, name + "1");
		Thread t2 = new SyncThread(lock3, name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private void quiz04() throws InterruptedException {
		Thread t1 = new SyncThread(lock4, name + "1");
		Thread t2 = new SyncThread(lock4, name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private void quiz05() throws InterruptedException {
		Thread t1 = new SyncThread(name + "1");
		Thread t2 = new SyncThread(name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private void quiz06() throws InterruptedException {
		Thread t1 = new SyncThread(this, name + "1");
		Thread t2 = new SyncThread(this, name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private void quiz07() throws InterruptedException {
		Thread t1 = new SyncThread(this.getClass(), name + "1");
		Thread t2 = new SyncThread(this.getClass(), name + "2");

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private static void quiz08() throws InterruptedException {
		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					SyncQuiz sync1 = new SyncQuiz("SyncA");
					sync1.quiz01();
				}
				catch(Exception ignored) {}
			}
		};

		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					SyncQuiz sync2 = new SyncQuiz("SyncB");
					sync2.quiz01();
				}
				catch(Exception ignored) {}
			}
		};

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private static void quiz09() throws InterruptedException {
		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					SyncQuiz sync1 = new SyncQuiz("SyncA");
					sync1.quiz03();
				}
				catch(Exception ignored) {}
			}
		};

		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					SyncQuiz sync2 = new SyncQuiz("SyncB");
					sync2.quiz03();
				}
				catch(Exception ignored) {}
			}
		};

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	private static void quiz10() throws InterruptedException {
		Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					SyncQuiz sync1 = new SyncQuiz("SyncA");
					sync1.quiz01();
				}
				catch(Exception ignored) {}
			}
		};

		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					SyncQuiz sync2 = new SyncQuiz("SyncB");
					sync2.quiz03();
				}
				catch(Exception ignored) {}
			}
		};

		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}

	public static void main(String[] args) throws InterruptedException {
		SyncQuiz quiz = new SyncQuiz("Sync");

		System.out.println("\nQuiz Question 01:");
		quiz.quiz01();

		System.out.println("\nQuiz Question 02:");
		quiz.quiz02();

		System.out.println("\nQuiz Question 03:");
		quiz.quiz03();

		System.out.println("\nQuiz Question 04:");
		quiz.quiz04();

		System.out.println("\nQuiz Question 05:");
		quiz.quiz05();

		System.out.println("\nQuiz Question 06:");
		quiz.quiz06();

		System.out.println("\nQuiz Question 07:");
		quiz.quiz07();

		System.out.println("\nQuiz Question 08:");
		quiz08();

		System.out.println("\nQuiz Question 09:");
		quiz09();

		System.out.println("\nQuiz Question 10:");
		quiz10();
	}
}