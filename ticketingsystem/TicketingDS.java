package ticketingsystem;

import java.util.*;
import java.util.concurrent.atomic.*;

public class TicketingDS implements TicketingSystem {
	int routenum = 10;
	int coachnum = 10;
	int seatnum = 100;
	int stationnum = 20;
	int threadnum = 64;

	AtomicBoolean state = new AtomicBoolean(false);

	AtomicLong tid = new AtomicLong(0);

	Train trains[];
    final boolean bypass = false;

	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		this.routenum = routenum;
		this.coachnum = coachnum;
		this.seatnum = seatnum;
		this.stationnum = stationnum;
		this.threadnum = threadnum;

		trains = new Train[routenum];
		for(int i=0; i<trains.length; i++) {
			trains[i] = new Train(i, coachnum, seatnum, stationnum);
		}

	}

	
	//ToDo
	void lock(){
		while(state.getAndSet(true)){}
	}
	void unlock(){
		state.set(false);
	}


	// =========== BE AWARE ============ 
	// ---------------------------------
	// ALL PARAMS INDEX STARTING FROM 1 
	// =================================
	public Ticket buyTicket(String passenger, int route, int departure, int arrival){
		if(bypass) return null;
		// lock();
		// try{
			Ticket t = trains[route-1].buyTicket(departure-1, arrival-1);
			if(t == null){
				// System.out.println("buy Ticket failed"+ route + " " + departure + " " + arrival);
				return null;
			}
			t.tid = tid.getAndIncrement();
			t.passenger = passenger;
			return t;
		// } finally {
		// 	unlock();
		// }
	}
	
	public int inquiry(int route, int departure, int arrival){
		if(bypass) return 1;

		// lock();
		// try{
			return trains[route-1].inquiry(departure-1, arrival-1);
		// } finally {
		// 	unlock();
		// }
	}

	public boolean refundTicket(Ticket t){
		if(bypass) return true;

		// lock();
		// try{
			trains[t.route-1].refundTicket(t);
			return true;
		// } finally {
		// 	unlock();
		// }
	}

	public boolean buyTicketReplay(Ticket t){
		return true;
	}
	public boolean refundTicketReplay(Ticket t){
		return true;
	}	
}
