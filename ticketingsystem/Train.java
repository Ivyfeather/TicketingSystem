package ticketingsystem;

import java.util.*;
import java.util.BitSet;
import java.util.concurrent.locks.*;

class Seat {
    //!! byte?
    // -1 means occupied
    int []steps;

    public Seat(int stationnum){
        steps = new int[stationnum];
        for(int i = 0; i < stationnum; i++){
            steps[i] = stationnum - i - 1;
        }

    }

    public boolean checkAvail(int dept, int arr){
        return steps[dept] >= (arr - dept);
    }

    public void orderSeat(int dept, int arr){
        int init = 0;
        for(int i=dept-1; i>=0 && steps[i]!=-1; i--) steps[i]=++init;
        for(int i=dept; i<arr; i++) steps[i]=-1;
    }

    public void clearSeat(int dept, int arr){
        int init = steps[arr];
        if(-1 == init) init = 0;
        for(int i=arr-1; i>=0 && ( i>=dept || steps[i]!= -1); i--){
            steps[i] = ++init;
        }
    }

    public int findLeft(int dept){
        if(0 == dept) return 0;
        int i;
        for(i=dept-1; i>=0; i--){
            if(-1 == steps[i]) return i+1;
        }
        return 0;

    }
    
    public int findRight(int arr){
        int stationnum = steps.length;
        int i;        
        for(i=arr; i<stationnum; i++){
            if(-1 == steps[i]) return i-1;
        }
        return stationnum-2;
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
            // System.err.println("lock " +i);
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
                    int right = seats[sid].findRight(arr);
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
        int right = seats[sid].findRight(arr);
        seats[sid].clearSeat(dept, arr);
	    inqTable.update(dept, arr, left, right, 1);

        locks[t.coach-1].unlock();

    }


}
