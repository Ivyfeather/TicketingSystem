package ticketingsystem;

import java.util.BitSet;

// class Seat {
//     // !! public
//     // public BitSet taken;
//     BitSet taken;

//     public Seat(int stationnum){
//         taken = new BitSet(stationnum);
//     }

//     public boolean checkAvail(int departure, int arrival){
//         // return true;
//         return taken.get(departure, arrival).isEmpty();
//     }

//     public void orderSeat(int departure, int arrival){
//         // return;
//         taken.set(departure, arrival);
//     }

//     public void clearSeat(int departure, int arrival){
//         // return;
//         taken.clear(departure, arrival);
//     }
    
// }

public class Train {
    int route;
    BitSet seats[];
    int coachnum = 8;
    int seatnum = 100;

    final boolean debug = false;

    public Train(int route, int coachnum, int seatnum, int stationnum){
        this.route = route;
        this.coachnum = coachnum;
        this.seatnum = seatnum;
        seats = new BitSet[coachnum*seatnum];
        for(int i=0; i<seats.length; i++){
            seats[i] = new BitSet(stationnum);
        }
    }

    public int inquiry(int departure, int arrival){
        int ticketsLeft = 0;
        for(int i=0; i<seats.length; i++){
            ticketsLeft += seats[i].get(departure, arrival).isEmpty()?1:0;
        }
        return ticketsLeft;
    }    

    public Ticket buyTicket(int departure, int arrival){
        for(int i=0; i<seats.length; i++){
            boolean tmp = seats[i].get(departure, arrival).isEmpty();
            if(tmp){
                seats[i].set(departure, arrival);
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
        // showSeatStatus();

        int sid = (t.coach-1)*seatnum + (t.seat-1);
        seats[sid].clear(t.departure-1, t.arrival-1);
    }


}