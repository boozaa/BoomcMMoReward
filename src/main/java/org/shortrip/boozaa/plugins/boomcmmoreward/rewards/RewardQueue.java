package org.shortrip.boozaa.plugins.boomcmmoreward.rewards;

import java.util.LinkedList;
import java.util.Queue;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;


public class RewardQueue {

	private static Queue<cReward> queue;

    
    public RewardQueue(){
    	queue = new LinkedList<cReward>();    	
    }
    
    public Queue<cReward> getQueue(){
    	return queue;
    }
    
    synchronized public void enqueue(cReward rew) {    	
    	if( rew != null){
    		queue.add(rew);
        	BoomcMMoReward.debug("Added one reward file to process on the Queue");
            notify();
    	}    	 
    }   

    synchronized public void sendNextReward() {
        //tant que la queue n'est pas vide
        while(!queue.isEmpty()) {
            try {
                //attente passive
                //wait();
            	BoomcMMoReward.debug("Queue head :" + queue.peek() );
            	BoomcMMoReward.debug("Queue size :" + queue.size() );
                BoomcMMoReward.debug("Processing reward file " + queue.peek().getName() + " on a new thread");        
                // On envoit le traitement du prochain cReward
                new RewardThread(queue.poll());
            	
            	
            } catch(Exception ie) {}
            
        }        
    }

	
	
}
