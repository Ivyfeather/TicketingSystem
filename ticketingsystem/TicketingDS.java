package ticketingsystem;

public class TicketingDS implements TicketingSystem {
	private int routenum = 5;
	private int coachnum = 8;
	private int seatnum = 100;
	private int stationnum = 10;
	private int threadnum = 16;

	Train trains[];

	public TicketingDS(int routenum, int coachnum, int seatnum, int stationnum, int threadnum) {
		this.routenum = routenum;
		this.coachnum = coachnum;
		this.seatnum = seatnum;
		this.stationnum = stationnum;
		this.threadnum = threadnum;

		trains = new Train[]


	}

	
	//ToDo
	public Ticket buyTicket(String passenger, int route, int departure, int arrival){

		return new Ticket();
	}
	
	public int inquiry(int route, int departure, int arrival){
		
		return 1;
	}

	public boolean refundTicket(Ticket ticket){



		return true;
	}

	public boolean buyTicketReplay(Ticket ticket){
		return true;
	}
	public boolean refundTicketReplay(Ticket ticket){
		return true;
	}	
}
