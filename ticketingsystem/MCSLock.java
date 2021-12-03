package ticketingsystem;

import java.util.concurrent.atomic.AtomicReference;
 
public class MCSLock{
	private AtomicReference<QNode> tail;
	ThreadLocal<QNode> myNode;
	
	public MCSLock(){
		tail = new AtomicReference<QNode>(null);
		myNode = new ThreadLocal<QNode>(){
			protected QNode initialValue(){
				return new QNode();
			}
		};
	}
	
	public void lock() {
		QNode qnode = myNode.get();
		QNode pred = tail.getAndSet(qnode);
		if(pred != null){
			qnode.locked = true;
			pred.next = qnode;
            // wait until predecessor gives up the lock
			while(qnode.locked){}
		}
	}
 
	public void unlock() {
		QNode qnode = myNode.get();
		if(qnode.next == null){
			if(tail.compareAndSet(qnode, null)){
				return;
			}
            // wait until successor fills in the next field 
			while(qnode.next == null){ }
		}
		qnode.next.locked = false;
		qnode.next = null;
	}
	
	public static class QNode {
		volatile boolean locked = false;
		volatile QNode next = null;
	}

}