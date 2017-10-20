package services.moleculer.strategies;

import java.security.SecureRandom;

import services.moleculer.services.ActionContainer;
import services.moleculer.services.Name;

/**
 * Java Random/SecureRandom-based invocation strategy.
 * 
 * @see RoundRobinStrategy
 * @see NanoSecRandomStrategy
 * @see XORShiftRandomStrategy
 */
@Name("Secure Random Strategy")
public final class SecureRandomStrategy extends ArrayBasedStrategy {

	// --- PROPERTIES ---

	private final SecureRandom rnd = new SecureRandom();

	// --- CONSTRUCTOR ---
	
	public SecureRandomStrategy(boolean preferLocal) {
		super(preferLocal);
	}
	
	// --- GET NEXT ACTION CONTAINER ---

	@Override
	public final ActionContainer next() {
		return actions[rnd.nextInt(actions.length)];
	}

}