package ticketingsystem;

import java.util.Bitset;

// Staring with Index 1

// !!?? check for IndexOutOfBoundary??
// !!?? arrival included??
class Seat {
    Bitset taken;

    Seat(int stationnum){
        taken = new Bitset(stationnum);
    }

    public int checkAvail(int departure, int arrival){
        if(taken.get(departure, arrival+1).isEmpty()){
            return 1;
        }
        return 0;
    }

    public void orderSeat(int departure, int arrival){
        taken.set(departure, arrival+1);
    }

    public void clearSeat(int departure, int arrival){
        taken.clear(departure, arrival+1);
    }
    
}

// !!?? consider deleting this layer
// directly init seats[coachnum*seatnum] in Train
class Coach {
    Seat seats[];

    Coach(int seatnum, int stationnum){
        seats = new Seat[seatnum];
        for(Seat s : seats){
            s = new Seat(stationnum);
        }
    }    
    public int inquiry(int departure, int arrival){
        int ticketsLeft = 0;
        for(Seat s : seats){
            ticketsLeft += s.checkAvail(departure, arrival);
        }
        return ticketsLeft;
    }    
    // return seatID
    public int buyTicket(int departure, int arrival){
        for(int id=0; id<seat.length; id++){
            if(seats[id].checkAvail(departure, arrival)){
                seats[id].orderSeat(departure, arrival);
                return id;
            }
        }
        return -1;
    }  

}

public class Train {
    Coach coaches[];

    Train(int coachnum, int seatnum, int stationnum){
        coaches = new Coach[coachnum];
        for(Coach c : coaches){
            c = new Coach(seatnum, stationnum);
        }
    }

    public int inquiry(int departure, int arrival){
        int ticketsLeft = 0;
        for(Coach c : coaches){
            ticketsLeft += c.inquiry(departure, arrival);
        }
        return ticketsLeft;
    }    

    public Ticket buyTicket(int departure, int arrival){
        for(int i=0; i<coaches.length; i++){
            int tmp = coaches[i].buyTicket(departure, arrival);
            if(tmp != -1){
                Ticket t = new Ticket();
                t.coach = i;
                t.seat = tmp;
                t.departure = departure;
                t.arrival = arrival;
            }
        }
        return null;
    }  


}