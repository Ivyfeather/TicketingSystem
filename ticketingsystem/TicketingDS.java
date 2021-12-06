package ticketingsystem;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.ConcurrentHashMap;

class SoldTickets{
    final ConcurrentHashMap<Long, Ticket> tickets = new ConcurrentHashMap<Long, Ticket>();
    
    public void add(Ticket t){
        if(null != tickets.put(t.tid, t)){
            System.err.println("ERROR: TID ALREADY EXISTS");
        }
    }

    public boolean checkAndRemove(Ticket refund){
		Ticket sold = tickets.get(refund.tid);
        // if first true, directly return false 
        if( null == sold) return false;
		if(!sold.passenger.equals(refund.passenger) ||
            sold.route != refund.route ||
            sold.coach != refund.coach ||
            sold.seat  != refund.seat  ||
            sold.departure != refund.departure ||
            sold.arrival != refund.arrival)
        {
            return false;
        }

        tickets.remove(refund.tid, refund);
        return true;

    }
}

public class TicketingDS implements TicketingSystem {
	AtomicLong tid = new AtomicLong(1);
	final SoldTickets sold = new SoldTickets();

	Train trains[];
    final boolean bypass = false;

	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		trains = new Train[routenum];
		for(int i=0; i<trains.length; i++) {
			trains[i] = new Train(i, coachnum, seatnum, stationnum);
		}

	}

	// =========== BE AWARE ============ 
	// ---------------------------------
	// ALL PARAMS INDEX STARTING FROM 1 
	// =================================
	public Ticket buyTicket(String passenger, int route, int departure, int arrival){
		if(bypass) return null;
		Ticket t = trains[route-1].buyTicket(departure-1, arrival-1);
		if(t == null){
			return null;
		}
		t.tid = tid.getAndIncrement();
		t.passenger = passenger;
		sold.add(t);
		return t;
	}
	
	public int inquiry(int route, int departure, int arrival){
		if(bypass) return 1;
		return trains[route-1].inquiry(departure-1, arrival-1);
	}

	public boolean refundTicket(Ticket t){
		if(bypass) return true;

		if(!sold.checkAndRemove(t)){
			return false;
		}
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
