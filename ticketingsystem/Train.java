package ticketingsystem;

import java.util.BitSet;

// !!?? check for IndexOutOfBoundary??
// !!?? arrival included??
class Seat {
    // !! public
    public BitSet taken;

    public Seat(int stationnum){
        taken = new BitSet(stationnum);
    }

    public boolean checkAvail(int departure, int arrival){
        return taken.get(departure, arrival).isEmpty();
    }

    public void orderSeat(int departure, int arrival){
        taken.set(departure, arrival);
    }

    public void clearSeat(int departure, int arrival){
        taken.clear(departure, arrival);
    }
    
}

public class Train {
    int route;
    Seat seats[];
    int coachnum = 8;
    int seatnum = 100;

    final boolean debug = false;

    public Train(int route, int coachnum, int seatnum, int stationnum){
        this.route = route;
        this.coachnum = coachnum;
        this.seatnum = seatnum;
        seats = new Seat[coachnum*seatnum];
        for(int i=0; i<seats.length; i++){
            seats[i] = new Seat(stationnum);
        }
    }

    public void showSeatStatus(){
        if(!debug) {return;}
        for(int i=0; i<seats.length; i++){
            System.err.println("Train " + route + " coach " + (i/seatnum+1) + " seat " + (i%seatnum+1) + " status " + seats[i].taken);
        }
    }

    public int testTest(){
        return 1;
    }

    public int inquiry(int departure, int arrival){
        showSeatStatus();
        int ticketsLeft = 0;
        for(int i=0; i<seats.length; i++){
            ticketsLeft += seats[i].checkAvail(departure, arrival)?1:0;
        }
        return ticketsLeft;
    }    

    public Ticket buyTicket(int departure, int arrival){
        // System.out.println("TE "+seats.length);
        showSeatStatus();

        for(int i=0; i<seats.length; i++){
            boolean tmp = seats[i].checkAvail(departure, arrival);
            if(tmp){
                seats[i].orderSeat(departure, arrival);
                Ticket t = new Ticket();
                t.route = route + 1;
                t.coach = (i/seatnum) + 1;
                t.seat = (i%seatnum) + 1;
                t.departure = departure + 1;
                t.arrival = arrival + 1;
                return t;
            }
        }
        
        return null;
    }  

    public void refundTicket(Ticket t){
        showSeatStatus();

        int sid = (t.coach-1)*seatnum + (t.seat-1);
        seats[sid].clearSeat(t.departure-1, t.arrival-1);
    }


}