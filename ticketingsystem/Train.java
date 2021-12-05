package ticketingsystem;

import java.util.*;
import java.util.BitSet;
import java.util.concurrent.locks.*;

class Seat {
    BitSet taken;

    public Seat(int stationnum){
        taken = new BitSet(stationnum);
    }

    public boolean checkAvail(int dept, int arr){
        if(taken.get(dept)) return false;
        return taken.get(dept+1, arr).isEmpty();
    }

    public void orderSeat(int dept, int arr){
        taken.set(dept, arr);
    }

    public void clearSeat(int dept, int arr){
        taken.clear(dept, arr);
    }

    public int findLeft(int dept){
        return (0 == dept)? 0 : taken.previousSetBit(dept-1) + 1;
    }

    // previous/nextSetBit returns -1 if no such Set Bit
    public int findRight(int arr, int stationnum){
        int tmp = taken.nextSetBit(arr) - 1;
        return (-2 == tmp)? stationnum - 2 : tmp;
    }
    
}

class InquiryTable{
    int stationnum;
    int [][] c;
    ReentrantReadWriteLock []rwl;
    Lock []r;
    Lock []w;

    public InquiryTable(int stationnum, int totalSeatnum){
        this.stationnum = stationnum;

        c = new int[stationnum][stationnum];
        int i,j;
        for(i=0; i<stationnum; i++){
            for(j=0; j<stationnum; j++){
                c[i][j] = totalSeatnum;
            }
        }

        rwl = new ReentrantReadWriteLock[stationnum];
        r = new Lock[stationnum];
        w = new Lock[stationnum];
        for(i = 0; i < rwl.length; i++) {
            rwl[i] = new ReentrantReadWriteLock();
            r[i] = rwl[i].readLock();
            w[i] = rwl[i].writeLock();
        }

    }

    public int inq(int dept, int arr){
        // System.err.println("inq "+dept+" "+arr+" "+c[dept][arr]);
        try{
            r[dept].lock();
            return c[dept][arr];
        } finally {
            r[dept].unlock();
        }
    }

    public void update(int dept, int arr, int left, int right, int inc){
        int i,j;
        for(i=left; i<arr; i++){
            w[i].lock();
        }
        for(i=left; i<arr; i++){
            for(j=dept+1; j<=right+1; j++){
                c[i][j] += inc;
            }
        }   
        for(i=left; i<arr; i++){
            w[i].unlock();
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

    MCSLock []locks;

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

        locks = new MCSLock[coachnum];
        for(int i = 0; i < locks.length; i++){
            locks[i] = new MCSLock();
        }

        inqTable = new InquiryTable(stationnum, coachnum*seatnum);
    }

    public int inquiry(int dept, int arr){
        return inqTable.inq(dept, arr);
    }    

    public Ticket buyTicket(int dept, int arr){
        if(inqTable.inq(dept, arr) <= 0){
            return null;
        }
        
        int i,j, sid;
        for(i=0; i<coachnum; i++){
            locks[i].lock();

            for(j=0; j<seatnum; j++){
                sid = i*seatnum + j;

                boolean tmp = seats[sid].checkAvail(dept, arr);
                if(tmp){
                    seats[sid].orderSeat(dept, arr);

                    int left = seats[sid].findLeft(dept);
                    int right = seats[sid].findRight(arr, stationnum);

                    inqTable.update(dept, arr, left, right, -1);
                    locks[i].unlock();
                    
                    Ticket t = new Ticket();
                    t.route = routeId + 1;
                    t.coach = i + 1;
                    t.seat = j + 1;
                    t.departure = dept + 1;
                    t.arrival = arr + 1;
                    return t;
                }
            } // end seat iter
            locks[i].unlock();
        } // end coach iter
        
        return null;
    }  

    public void refundTicket(Ticket t){

        int sid = (t.coach-1)*seatnum + (t.seat-1);
        int dept = t.departure - 1;
        int arr = t.arrival - 1;    

        locks[t.coach-1].lock();

        int left = seats[sid].findLeft(dept);
        int right = seats[sid].findRight(arr, stationnum);

        seats[sid].clearSeat(dept, arr);
	    inqTable.update(dept, arr, left, right, 1);
        locks[t.coach-1].unlock();

    }


}
