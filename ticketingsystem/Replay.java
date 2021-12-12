package ticketingsystem;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;



public class Replay{
    static int threadNum;
    static List<String> methodList = new ArrayList<String>();
    
/**********Manually Modified ***********/
    static boolean isPosttime = true;
    static boolean detail = false;
    final static int routenum = 3;
    final static int coachnum = 3;
    final static int seatnum = 3;
    final static int stationnum = 3;
    
    static int debugMode = 1;

	final static boolean printDebug = false;

    static ArrayList<HistoryLine> history = new ArrayList<HistoryLine>();
	static int callId = 0; // unique call id 
	static Ticket lastWrongBuy;

    static TicketingDS object;

	public static class HistoryComparator implements Comparator<HistoryLine>{
		@Override
		public int compare(HistoryLine h1, HistoryLine h2){
			long h1Time = (h1.isReturn)? h1.posttime : h1.pretime;
			long h2Time = (h2.isReturn)? h2.posttime : h2.pretime;
			if(h1Time - h2Time > 0)
				return 1;
			else if(h1Time - h2Time == 0)
				return 0;
			else
				return -1;
		};
	}

    public static class HistoryLine{
		long pretime;
		long posttime;
		int threadid;
		String operationName;
		long tid;
		String passenger;
		int route;
		int coach;
		int seat;
		int departure;
		int arrival;
		String res;
		boolean isReturn;
		int callId;

		public HistoryLine(){}
		public HistoryLine(HistoryLine h){
			this.pretime = h.pretime;
			this.posttime = h.posttime;
			this.threadid = h.threadid;
			this.operationName = h.operationName;
			this.tid = h.tid;
			this.passenger = h.passenger;
			this.route = h.route;
			this.coach = h.coach;
			this.seat = h.seat;
			this.departure = h.departure;
			this.arrival = h.arrival;
			this.res = h.res;
			this.isReturn = h.isReturn;
			this.callId = h.callId;
		}
    }
// Scanning line and parse history
    private static boolean parseline(ArrayList<HistoryLine> historyList, String line) {
		Scanner linescanner = new Scanner(line);
		if (line.equals("")) {
			linescanner.close();
			return true;
		}
		// add method call
		HistoryLine t1 = new HistoryLine();
		t1.pretime = linescanner.nextLong();
		t1.posttime = linescanner.nextLong();
		t1.threadid = linescanner.nextInt();
		t1.operationName = linescanner.next();
		t1.tid = linescanner.nextLong();
		t1.passenger = linescanner.next();
		t1.route = linescanner.nextInt();
		t1.coach = linescanner.nextInt();
		t1.departure = linescanner.nextInt();
		t1.arrival = linescanner.nextInt();	
		t1.seat = linescanner.nextInt();
		t1.res = linescanner.next();
		t1.callId = callId;
		
		// add method return 
		HistoryLine t2 = new HistoryLine(t1);
		t2.callId = callId ++;
		t2.isReturn = true;
		
		t1.posttime = 0;
		t2.pretime = 0;
		historyList.add(t1);
		historyList.add(t2);

		linescanner.close();
		return true;
    }

    private static void initialization(){
		object = new TicketingDS(routenum, coachnum, seatnum, stationnum, threadNum);
		methodList.add("refundTicket");
		methodList.add("buyTicket");
		methodList.add("inquiry");
		methodList.add("refundTicketReplay");
		methodList.add("buyTicketReplay");
    }
    private static boolean execute(String methodName, HistoryLine line, int line_num){
		Ticket ticket = new Ticket();
		boolean flag = false;
		ticket.tid = line.tid;
		ticket.passenger = line.passenger;
		ticket.route = line.route;
		ticket.coach = line.coach;
		ticket.departure = line.departure;
		ticket.arrival = line.arrival;
		ticket.seat = line.seat;
		if(methodName.equals("buyTicket")){
			if(line.res.equals("false")){
				int num = object.inquiry(ticket.route, ticket.departure, ticket.arrival);
				if(num == 0)
					return true;
				else {
					System.out.println("Error: TicketSoldOut" + " " + line.pretime + " " + line.posttime + " " + line.threadid + " " + line.route + " " + line.departure + " " + line.arrival);
					System.out.println("RemainTicket" + " " + num + " " + line.route + " " + line.departure + " " + line.arrival);
					return false;
				}
			}
			Ticket ticket1 = new Ticket();
			ticket1 = object.buyTicket(ticket.passenger, ticket.route, ticket.departure, ticket.arrival);
			if(ticket1 != null && line.res.equals("true") &&
				ticket.passenger == ticket1.passenger && ticket.route == ticket1.route &&
				ticket.coach == ticket1.coach && ticket.departure == ticket1.departure &&
				ticket.arrival == ticket1.arrival && ticket.seat == ticket1.seat) {
				return true;
			} else {
				System.out.println("Error: Ticket is bought" + " " + line.pretime + " " + line.posttime + " " + line.threadid + " " + ticket.tid + " " + ticket.passenger + " " + ticket.route + " " + ticket.coach    + " " + ticket.departure + " " + ticket.arrival + " " + ticket.seat);
				lastWrongBuy = ticket1;
				return false;
			}
		}
		else if(methodName.equals("refundTicket")){
			flag = object.refundTicket(ticket);
			if((flag && line.res.equals("true")) || (!flag && line.res.equals("false")))
				return true;
			else {
				System.out.println("Error: Ticket is refunded" + " " + line.pretime + " " + line.posttime + " " + line.threadid + " " + ticket.tid + " " + ticket.passenger + " " + ticket.route + " " + ticket.coach    + " " + ticket.departure + " " + ticket.arrival + " " + ticket.seat);
				return false;
			}
		}
		else if(methodName.equals("inquiry")){
			int num = object.inquiry(line.route, line.departure, line.arrival);
			if(num == line.seat)
				return true;
			else {
				System.out.println("Error: RemainTicket" + " " + line.pretime + " " + line.posttime + " " + line.threadid + " " + line.route + " " + line.departure + " " + line.arrival + " " + line.seat);
				System.out.println("Real RemainTicket is" + " " + line.seat + " " + ", Expect RemainTicket is" + " " + num + ", " + line.route + " " + line.departure + " " + line.arrival);
				return false;
			}
		}
		else if(methodName.equals("buyTicketReplay")){
			flag = object.buyTicketReplay(lastWrongBuy);
			System.out.println("Trying BuyReplay");
			if(flag) return true;
			else{
				System.out.println("Error: buyTicketReplay Failed");
				return false;
			}
		}
		else if(methodName.equals("refundTicketReplay")){
			flag = object.refundTicketReplay(ticket);
			System.out.println("Trying RefundReplay");
			if(flag) return true;
			else{
				System.out.println("Error: refundTicketReplay Failed");
				return false;
			}
		}
		System.out.println("No match method name");
		return false;
    }
/***********************VeriLin*************** */

    private static void writeHistoryToFile(ArrayList<HistoryLine> historyList, String filename) {
		try {
			System.setOut(new PrintStream(new FileOutputStream(filename)));
			writeHistory(historyList);
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}catch (FileNotFoundException e) {
			System.out.println(e);
		}
    }
    private static void writeHistory(ArrayList<HistoryLine> historyList) {
		for (int i = 0; i < historyList.size(); i++) {
			writeline(historyList, i);
		}
    }
    private static void writeline(ArrayList<HistoryLine> historyList, int line) {
		HistoryLine tl = historyList.get(line);
		System.out.println(tl.callId + " " +tl.pretime + " " + tl.posttime + " " + tl.threadid + " " + tl.operationName + " " + tl.tid + " " + tl.passenger + " " + tl.route + " " + tl.coach    + " " + tl.departure + " " + tl.arrival + " " + tl.seat + " " + tl.res);
    }
    private static boolean readHistory(ArrayList<HistoryLine> historyList, String filename) {
		try {
			Scanner scanner = new Scanner(new File(filename));
			int i = 0;
			while (scanner.hasNextLine()) {
				if(parseline(historyList, scanner.nextLine()) == false) {
					scanner.close();
					System.out.println("Error in parsing line " + i);
					return false;
				}
				i++;
			}
			scanner.close();
		}catch (FileNotFoundException e) {
			System.out.println(e);
		}
		return true;
	}

	private static boolean checkline(ArrayList<HistoryLine> historyList, int index){
		HistoryLine line = historyList.get(index);
		if(line.isReturn) return true;
		
		if (debugMode == 1) {
			if (index == 158) {
				System.out.println("Debugging line " + index + " ");
			}
		}
		

		for(int i = 0; i < methodList.size();i++){
			if(line.operationName.equals(methodList.get(i))){
				boolean flag = execute(methodList.get(i), line, index);
				// System.out.println("Line " + index + " executing " + methodList.get(i) + " res: " + flag + " tid = " + line.tid);
				return flag;
			}
		}
		return false;
		
    }
 
	public static boolean isLinearizable(ArrayList<HistoryLine> historyList) {
		if(historyList.isEmpty()) {
			return true;
		}

		for(int i = 0; i < historyList.size(); i++){
			HistoryLine line = historyList.get(i);
			if(line.isReturn) break;

			writeline(historyList, i);
			
			if(checkline(historyList, i)){
				System.out.println("This is the first move");
				ArrayList<HistoryLine> next = new ArrayList<HistoryLine>(historyList);
				
				// find corresponding call and return
				// then remove them from historylist, check the rest
				int callId = line.callId, j;
				for(j = i + 1; j < historyList.size(); j++){
					if(historyList.get(j).callId == callId) break;
				}	
				next.remove(i);
				// ==== WARING ==== 
				// AFTER REMOVING i, THE INDEX OF j DECREASES 1
				next.remove(j-1);

				if(isLinearizable(next))
					return true;
			}
					
			System.out.println("Try Next");

			if(line.operationName.equals("buyTicket")){
				execute("buyTicketReplay", line, i);
			}
			else if(line.operationName.equals("refundTicket")){
				execute("refundTicketReplay", line, i);
			}
			else{
				System.out.println("No match REPLAY");
				return false;
			}
		}
		return false;
	}


    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException{
		if(args.length != 4){
			System.out.println("The parameter list of VeriLin is threadNum, historyFile, isPosttime(0/1), failedTrace.");
			return;
		}
		threadNum = Integer.parseInt(args[0]);
		String fileName = args[1];
		if(Integer.parseInt(args[2]) == 0){
			isPosttime = false;
		}
		else if(Integer.parseInt(args[2]) == 1){
			isPosttime = true;
		}
		else{
			System.out.println("The parameter list of VeriLin is threadNum, historyFile, isPosttime(0/1), failedTrace.");
			return;
		}
		String ft = args[3];
		long startMs, endMs;
		readHistory(history, fileName);
		initialization();
		startMs = System.currentTimeMillis();

		HistoryComparator com = new HistoryComparator();
		Collections.sort(history, com);
		
		writeHistoryToFile(history, ft);

		int callCount = 0;
		boolean passed = true;
		ArrayList<HistoryLine> overlap = new ArrayList<HistoryLine>();

		try{
			if(!printDebug) System.setOut(new PrintStream(new FileOutputStream("/dev/null")));
		} catch(FileNotFoundException e) {
			System.out.println(e);
		}

		for(int i = 0; i < history.size(); i++){
			HistoryLine line = history.get(i);
			overlap.add(line);

			callCount += (line.isReturn)? -1 : 1;

			if(0 == callCount){ // check overlap linearable
				if(!isLinearizable(overlap)){
					passed = false;
					break;
				}
				overlap.clear();
				if(printDebug) System.out.println("Passed this overlap~");
			}

		}
		endMs = System.currentTimeMillis();
		if(!printDebug) System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

		if(passed) System.out.println("Linearization Check Passed");
		else System.out.println("Linearization Check Failed");
		System.out.println("checking time = " + (endMs - startMs));
    }
}
