package ticketingsystem;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;

<<<<<<< HEAD
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
        if( null == sold){
			System.err.println("Check Sold Empty");
 			return false;
		}
		if(!sold.passenger.equals(refund.passenger) ||
            sold.route != refund.route ||
            sold.coach != refund.coach ||
            sold.seat  != refund.seat  ||
            sold.departure != refund.departure ||
            sold.arrival != refund.arrival)
        {
			System.err.println("Check Sold Mismatch");
            return false;
        }

        tickets.remove(refund.tid, refund);
        return true;

    }
}

=======
>>>>>>> d9e900d71213004e3320be7461e090884a55ffa0
public class TicketingDS implements TicketingSystem {
	AtomicLong tid = new AtomicLong(1);
	final SoldTickets sold = new SoldTickets();
	// ReentrantLock trainLock[];

	Train trains[];
    final boolean bypass = false;

	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		trains = new Train[routenum];
		// trainLock = new ReentrantLock[routenum];
		for(int i=0; i<trains.length; i++) {
			trains[i] = new Train(i, coachnum, seatnum, stationnum);
			// trainLock[i] = new ReentrantLock();
		}
	}


	// =========== BE AWARE ============ 
	// ---------------------------------
	// ALL PARAMS INDEX STARTING FROM 1 
	// =================================
	public Ticket buyTicket(String passenger, int route, int departure, int arrival){
		if(bypass) return null;
		// trainLock[route-1].lock();
		
		Ticket t = trains[route-1].buyTicket(departure-1, arrival-1);
		if(t == null){
			// trainLock[route-1].unlock();
			return null;
		}
		t.tid = tid.getAndIncrement();
		t.passenger = passenger;
		// sold.add(t);
		// trainLock[route-1].unlock();

		return t;
	}
	
	public int inquiry(int route, int departure, int arrival){
		if(bypass) return 1;
		return trains[route-1].inquiry(departure-1, arrival-1);
	}

	public boolean refundTicket(Ticket t){
		if(bypass) return true;
		// trainLock[t.route-1].lock();

		// if(!sold.checkAndRemove(t)){
		// 	return false;
		// }
		trains[t.route-1].refundTicket(t);
		// trainLock[t.route-1].unlock();

		return true;
	}

	public boolean buyTicketReplay(Ticket t){
		// buyTicket failed, so buyTicketReplay does nothing
		if(null == t) return true;
		return trains[t.route-1].buyTicketReplay(t);
	}

	public boolean refundTicketReplay(Ticket t){
		return trains[t.route-1].refundTicketReplay(t);
	}	
}

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

