/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch;
/**
 * The states for a distributed transaction system.
 */
//@formatter:off
public enum TxState {
	//OLTP States
	MARKED_ABORT 	(false, false),
	LOCALLY_DONE 	(false, false),
	COMMITTED 		(false, false),
	ABORTED 		(false, false),
	ABANDONED		(false, false),
	
	
	//Recoverable States
	TERMINATED 		(false, true),
	HEUR_COMMITTED 	("HEURISTIC COMMIT", 	true, 	false, TERMINATED),
	
	/**
	 * @deprecated TODO replace by COMMITING or ABORTING where relevant
	 */
	HEUR_HAZARD 	("HEURISTIC HAZARD", 	false, 	false, TERMINATED),
	HEUR_ABORTED 	("HEURISTIC ROLLBACK", 	true, 	false, TERMINATED),
	HEUR_MIXED 		("HEURISTIC MIXED", 	true, 	false, TERMINATED),	
	COMMITTING 		(						true, 	false, HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED, TERMINATED),
	ABORTING 	 	("ROLLING BACK",		false, 	false, HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED, TERMINATED),
	IN_DOUBT  	 	("PREPARED", 			true, 	false, ABORTING, COMMITTING, TERMINATED),
	PREPARING 	 	(						false, 	false, IN_DOUBT, ABORTING, TERMINATED),
	ACTIVE 		 	(						false, 	false, ABORTING, COMMITTING, PREPARING);

	private String label;
	
	private boolean recoverableState;
	
	private boolean finalState;
	
	private TxState[] legalNextStates;
	
	TxState (boolean recoverableState, boolean finalState, TxState... legalNextStates) {
		this.label=name();
		this.finalState=finalState;
		this.recoverableState=recoverableState;
		this.legalNextStates=legalNextStates;
	}
	TxState (String label, boolean recoverableState, boolean finalState, TxState... legalNextStates) {
		this.label=label;
		this.finalState=finalState;
		this.recoverableState=recoverableState;
		this.legalNextStates=legalNextStates;
	}
	
	public boolean isFinalState() {
		return finalState;
	}
	
	
	public boolean isFinalStateForOltp() {
		return isFinalState() || this == ABANDONED || isHeuristic();
	}
	
	public boolean isRecoverableState() {
		return recoverableState;
	}
	
	public boolean transitionAllowedTo(TxState nextState) {
		//transition to the same state...
		if(nextState == this) {
			return true;
		}
			
		for (TxState txState : legalNextStates) {
			if(txState == nextState) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOneOf(TxState... state) {
		for (int i = 0; i < state.length; i++) {
			if(this==state[i])
				return true;
		}
		return false;
	}
	
	public String label() {
		return label;
	}
	
	public boolean isHeuristic() {
		return isOneOf(HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED);
	}
	
}
