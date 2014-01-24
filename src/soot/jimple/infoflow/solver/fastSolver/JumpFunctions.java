/*******************************************************************************
 * Copyright (c) 2012 Eric Bodden.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eric Bodden - initial API and implementation
 ******************************************************************************/
package soot.jimple.infoflow.solver.fastSolver;

import heros.SynchronizedBy;
import heros.ThreadSafe;
import heros.solver.PathEdge;

import java.util.Map;

import com.google.common.collect.Maps;


/**
 * The IDE algorithm uses a list of jump functions. Instead of a list, we use a set of three
 * maps that are kept in sync. This allows for efficient indexing: the algorithm accesses
 * elements from the list through three different indices.
 */
@ThreadSafe
public class JumpFunctions<N,D> {
		
	//mapping from target node and value to a list of all source values and associated functions
	//where the list is implemented as a mapping from the source value to the function
	//we exclude empty default functions
	@SynchronizedBy("consistent lock on this")
	protected Map<WeakPathEdge<N, D>,WeakPathEdge<N, D>> nonEmptyReverseLookup = Maps.newHashMap();
	
	public JumpFunctions() {
	}

	/**
	 * Records a jump function. The source statement is implicit.
	 * @see PathEdge
	 */
	public D addFunction(WeakPathEdge<N, D> edge) {
		synchronized (this) {
			WeakPathEdge<N, D> existingVal = nonEmptyReverseLookup.get(edge);
			if (existingVal != null) {
				if (existingVal.isDead())
					nonEmptyReverseLookup.remove(existingVal);
				return existingVal.factAtTarget();
			}
			nonEmptyReverseLookup.put(edge, edge);
			return null;
		}
	}
	
	/**
	 * Removes all jump functions
	 */
	public synchronized void clear() {
		this.nonEmptyReverseLookup.clear();
	}

}
