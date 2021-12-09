package ticketingsystem;

class Ticket{
	long tid;
	String passenger;
	int route;
	int coach;
	int seat;
	int departure;
	int arrival;

	public void print(){
		System.err.println("Ticket "+ tid + " "+ passenger + " " + route + " " + coach + " " + departure+ " "+arrival + " " + seat );
	}
}


public interface TicketingSystem {
	Ticket buyTicket(String passenger, int route, int departure, int arrival);
	int inquiry(int route, int departure, int arrival);
	boolean refundTicket(Ticket ticket);
	boolean buyTicketReplay(Ticket ticket);
	boolean refundTicketReplay(Ticket ticket);

}
