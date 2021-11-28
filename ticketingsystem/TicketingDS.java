package ticketingsystem;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class TicketingDS implements TicketingSystem {
	int routenum = 5;
	int coachnum = 8;
	int seatnum = 100;
	int stationnum = 10;
	int threadnum = 16;

	static AtomicLong tid;

	Train trains[];

	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		this.routenum = routenum;
		this.coachnum = coachnum;
		this.seatnum = seatnum;
		this.stationnum = stationnum;
		this.threadnum = threadnum;


		trains = new Train[routenum];
		for(int i=0; i<trains.length; i++) {
			trains[i] = new Train(coachnum, seatnum, stationnum);
		}


		
	}

	
	//ToDo
	// =========== BE AWARE ============ 
	// ---------------------------------
	// ALL PARAMS INDEX STARTING FROM 1 
	// =================================
	public Ticket buyTicket(String passenger, int route, int departure, int arrival){
		Ticket t = trains[route-1].buyTicket(departure-1, arrival-1);
		if(t == null){
			return null;
		}
		t.tid = tid.getAndIncrement();
		t.passenger = passenger;
		return t;
	}
	
	public int inquiry(int route, int departure, int arrival){
		return trains[route-1].inquiry(departure-1, arrival-1);
	}

	public boolean refundTicket(Ticket t){
		trains[t.route-1].refundTicket(t);
		return true;
	}

	public boolean buyTicketReplay(Ticket t){
		return true;
	}
	public boolean refundTicketReplay(Ticket t){
		return true;
	}	
}
