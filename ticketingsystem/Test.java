package ticketingsystem;

public class Test {
	final static int threadnum = 4;
	final static int routenum = 3; // route is designed from 1 to 3
	final static int coachnum = 5; // coach is arranged from 1 to 5
	final static int seatnum = 10; // seat is allocated from 1 to 20
	final static int stationnum = 8; // station is designed from 1 to 5

	public static void main(String[] args) throws InterruptedException {
        
		final TicketingDS tds = new TicketingDS(routenum, coachnum, seatnum, stationnum, threadnum);

		//ToDo
	    
	}
}
