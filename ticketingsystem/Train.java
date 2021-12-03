package ticketingsystem;

import java.util.*;
import java.util.BitSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Seat {
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

class InquiryTable{
    int stationnum;
    int [][] c;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    final Lock r = rwl.readLock();
    final Lock w = rwl.writeLock();

    public InquiryTable(int stationnum, int totalSeatnum){
        this.stationnum = stationnum;

        c = new int[stationnum][stationnum];
        int i,j;
        for(i=0; i<stationnum; i++){
            for(j=0; j<stationnum; j++){
                c[i][j] = totalSeatnum;
            }
        }
    }

    public int inq(int dept, int arr){
        // System.err.println("inq "+dept+" "+arr+" "+c[dept][arr]);
        try{
            r.lock();
            return c[dept][arr];
        } finally {
            r.unlock();
        }
    }

    public void update(int dept, int arr, int left, int right, int inc){
        try{
            w.lock();
            int i,j;
            for(i=left; i<arr; i++){
                for(j=dept+1; j<=right+1; j++){
                    c[i][j] += inc;
                }
            }   
        } finally {
            w.unlock();
        }
    }

}


public class Train {
    int routeId;
    int coachnum = 8;
    int seatnum = 100;
    int stationnum = 10;

    Seat []seats;
    InquiryTable inqTable;

    final ReentrantLock locks = new ReentrantLock();

    final boolean debug = false;

    public Train(int routeId, int coachnum, int seatnum, int stationnum){
        this.routeId = routeId;
        this.coachnum = coachnum;
        this.seatnum = seatnum;
        this.stationnum = stationnum;

        seats = new Seat[coachnum*seatnum];
        for(int i=0; i<seats.length; i++){
            seats[i] = new Seat(stationnum);
        }

        //!!!!
        // locks = new MCSLock[coachnum*seatnum/perLock];
        // for(int i = 0; i < locks.length; i++){
        //     System.err.println("lock " +i);
        //     locks[i] = new MCSLock();
        // }

        inqTable = new InquiryTable(stationnum, coachnum*seatnum);
    }

    public int inquiry(int departure, int arrival){
        return inqTable.inq(departure, arrival);
    }    

    public Ticket buyTicket(int departure, int arrival){
        if(inqTable.inq(departure, arrival) <= 0){
            return null;
        }

        

        int length = seats.length;
        for(int i=0; i<length; i++){
            locks.lock();

            boolean tmp = seats[i].checkAvail(departure, arrival);
            if(tmp){
                seats[i].orderSeat(departure, arrival);
                Ticket t = new Ticket();
                t.route = routeId + 1;
                t.coach = (i/seatnum) + 1;
                t.seat = (i%seatnum) + 1;
                t.departure = departure + 1;
                t.arrival = arrival + 1;
                // System.err.println("HELLEN");
                int left, right;

                left = (0 == departure) ? 0 : seats[i].taken.previousSetBit(departure-1) + 1;
                right = seats[i].taken.nextSetBit(arrival) - 1;
                if(-2 == right) right = stationnum - 2;

                inqTable.update(departure, arrival, left, right, -1);
                locks.unlock();

                return t;
            }

            locks.unlock();
        }
        
        return null;
    }  

    public void refundTicket(Ticket t){

        int i = (t.coach-1)*seatnum + (t.seat-1);
        int dept = t.departure - 1;
        int arr = t.arrival - 1;    

        // returns -1 if no such Set Bit
        int left, right;


        locks.lock();

        left = (0 == dept) ? 0 : seats[i].taken.previousSetBit(dept-1) + 1;
        right = seats[i].taken.nextSetBit(arr) - 1;
        if(-2 == right) right = stationnum - 2;

        // System.err.println("WTLL");

        seats[i].clearSeat(dept, arr);
	    inqTable.update(dept, arr, left, right, 1);
        locks.unlock();

    }


}
