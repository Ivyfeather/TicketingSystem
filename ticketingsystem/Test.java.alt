package ticketingsystem;

import java.util.*;

import java.util.concurrent.atomic.AtomicInteger;

class ThreadIdtest {
	// Atomic integer containing the next thread ID to be assigned
	private static final AtomicInteger nextId = new AtomicInteger(0);

	// Thread local variable containing each thread's ID
	private static final ThreadLocal<Integer> threadId =
			new ThreadLocal<Integer>() {
				@Override protected Integer initialValue() {
					return nextId.getAndIncrement();
				}
			};

	// Returns the current thread's unique ID, assigning it if necessary
	public static int get() {
		return threadId.get();
	}
}

public class Test {
	static int threadnum = 2; //input
	final static int routenum = 10; // route is designed from 1 to 3
	final static int coachnum = 10; // coach is arranged from 1 to 5
	final static int seatnum = 100; // seat is allocated from 1 to 20
	final static int stationnum = 20; // station is designed from 1 to 5

	static int testnum = 1000000; //input
	final static int retpc = 10; // return ticket operation is 10% percent
	final static int buypc = 30; // buy ticket operation is 20% percent
	final static int inqpc = 100; //inquiry ticket operation is 70% percent



	static String passengerName() {
		Random rand = new Random();
		long uid = rand.nextInt(1000);
		return "passenger" + uid;
	}

	public static void main(String[] args) throws InterruptedException {
		/////////脚本输入
		if (args.length != 2)
		{
			System.out.println("The arguments of GenerateHistory is threadNum,  testnum");
			return;
		}
		threadnum = Integer.parseInt(args[0]);
		testnum = Integer.parseInt(args[1]);     //脚本输入
		////////////脚本输入

		Thread[] threads = new Thread[threadnum];

		final TicketingDS tds = new TicketingDS(routenum, coachnum, seatnum, stationnum, threadnum);

		final long startTime = System.nanoTime();
		long start = System.currentTimeMillis();

		for (int i = 0; i< threadnum; i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					Random rand = new Random();
					Ticket ticket = new Ticket();
					ArrayList<Ticket> soldTicket = new ArrayList<Ticket>();

					for (int i = 0; i < testnum; i++) {
						int sel = rand.nextInt(inqpc);
						if (0 <= sel && sel < retpc && soldTicket.size() > 0) { // return ticket
							int select = rand.nextInt(soldTicket.size());
							if ((ticket = soldTicket.remove(select)) != null) {
								long preTime = System.nanoTime() - startTime;
								if (tds.refundTicket(ticket)) {
								} else {
									System.out.println(preTime + " " + String.valueOf(System.nanoTime()-startTime) + " " + ThreadIdtest.get() + " " + "ErrOfRefund");
									System.out.flush();
								}
							} else {
								long preTime = System.nanoTime() - startTime;
								System.out.println(preTime + " " + String.valueOf(System.nanoTime()-startTime) + " " + ThreadIdtest.get() + " " + "ErrOfRefund");
								System.out.flush();
							}
						} else if (retpc <= sel && sel < buypc) { // buy ticket
							String passenger = passengerName();
							int route = rand.nextInt(routenum) + 1;
							int departure = rand.nextInt(stationnum - 1) + 1;
							int arrival = departure + rand.nextInt(stationnum - departure) + 1; // arrival is always greater than departure
							if ((ticket = tds.buyTicket(passenger, route, departure, arrival)) != null) {
								long postTime = System.nanoTime() - startTime;
								soldTicket.add(ticket);
							}
						} else if (buypc <= sel && sel < inqpc) { // inquiry ticket

							int route = rand.nextInt(routenum) + 1;
							int departure = rand.nextInt(stationnum - 1) + 1;
							int arrival = departure + rand.nextInt(stationnum - departure) + 1; // arrival is always greater than departure
							int leftTicket = tds.inquiry(route, departure, arrival);

						}
					}

				}
			});
			threads[i].start();
		}

		for (int i = 0; i< threadnum; i++) {
			threads[i].join();
		}
		long stop = System.currentTimeMillis();
		System.out.println("Time: "+(stop - start)+" ms");
		System.out.println("ops: "+(threadnum*testnum)/(stop - start));

	}
}
