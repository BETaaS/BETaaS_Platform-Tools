package eu.betaas.taas.qosmanager.api.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import eu.betaas.taas.qosmanager.api.QoSRankList;

public class HeuristicAsync implements Runnable {
	private final Heuristic heur;
	private final HeuristicAssured heurass;
	private final ExternalAPIImpl externalAPIImpl;
	private final ExecutorService executor;
	private final boolean assured_empty;
	public HeuristicAsync(Heuristic heur, ExecutorService executor, HeuristicAssured heurass, ExternalAPIImpl externalAPIImpl, boolean assured_empty){
		this.heur = heur;
		this.executor = executor;
		this.heurass = heurass;
		this.externalAPIImpl = externalAPIImpl;
		this.assured_empty = assured_empty;
	}
	public void run() {
		
		try {
			Future<QoSRankList> future = this.executor.submit(heur);
			QoSRankList alloc = future.get();
			externalAPIImpl.setAllocation(alloc);
			externalAPIImpl.update_data(alloc);
			if(!assured_empty){
				future = this.executor.submit(heurass);
				QoSRankList assured_allocation = future.get();
				alloc.setAssignmentsMap(assured_allocation.getAssignmentsMap());
				alloc.setEquivalentsMap(assured_allocation.getEquivalentsMap());
				alloc.setRequestsMap(assured_allocation.getRequestsMap());
				alloc.setThingServicesMap(assured_allocation.getThingServicesMap());
				alloc.setAssuredRequestsMap(assured_allocation.getAssuredRequestsMap());
				alloc.setThingsMap(assured_allocation.getThingsMap());
				externalAPIImpl.update_data(alloc);
				externalAPIImpl.mergeSchemas(assured_allocation);
			}
			externalAPIImpl.sendNotification();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
