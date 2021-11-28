package ticketingsystem;

import java.util.BitSet;

// !!?? check for IndexOutOfBoundary??
// !!?? arrival included??
class Seat {
    BitSet taken;

    public Seat(int stationnum){
        taken = new BitSet(stationnum);
    }

    public boolean checkAvail(int departure, int arrival){
        return taken.get(departure, arrival+1).isEmpty();
    }

    public void orderSeat(int departure, int arrival){
        taken.set(departure, arrival+1);
    }

    public void clearSeat(int departure, int arrival){
        taken.clear(departure, arrival+1);
    }
    
}

public class Train {
    Seat seats[];
    int coachnum = 8;
    int seatnum = 100;

    public Train(int coachnum, int seatnum, int stationnum){
        this.coachnum = coachnum;
        this.seatnum = seatnum;
        seats = new Seat[coachnum*seatnum];
        for(int i=0; i<seats.length; i++){
            seats[i] = new Seat(stationnum);
        }
    }

    public int testTest(){
        return 1;
    }

    public int inquiry(int departure, int arrival){
        int ticketsLeft = 0;
        for(int i=0; i<seats.length; i++){
            ticketsLeft += seats[i].checkAvail(departure, arrival)?1:0;
        }
        return ticketsLeft;
    }    

    public Ticket buyTicket(int departure, int arrival){
        for(int i=0; i<seats.length; i++){
            boolean tmp = seats[i].checkAvail(departure, arrival);
            if(tmp){
                seats[i].orderSeat(departure, arrival);
                Ticket t = new Ticket();
                t.coach = (i/seatnum) + 1;
                t.seat = (i%seatnum) + 1;
                t.departure = departure + 1;
                t.arrival = arrival + 1;
            }
        }
        return null;
    }  

    public void refundTicket(Ticket t){
        int sid = (t.coach-1)*seatnum + t.seat;
        seats[sid].clearSeat(t.departure-1, t.arrival-1);
    }


}