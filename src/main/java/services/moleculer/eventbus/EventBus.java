package services.moleculer.eventbus;

import org.slf4j.Logger;

import services.moleculer.ServiceBroker;
import services.moleculer.logger.AsyncLoggerFactory;
import services.moleculer.utils.MoleculerComponent;

public abstract class EventBus implements MoleculerComponent {

	// --- NAME OF THE MOLECULER COMPONENT ---
	
	@Override
	public String name() {
		return "Event Bus";
	}

	// --- LOGGER ---

	protected final Logger logger;

	// --- CONSTUCTOR ---

	public EventBus() {
		logger = AsyncLoggerFactory.getLogger(name());
	}
	
	// --- INIT EVENT BUS ---
	
	@Override
	public void init(ServiceBroker broker) throws Exception {
	}

	// --- STOP EVENT BUS ---
	
	@Override
	public void close() {
	}

	// --- REGISTER LISTENER ----
	
	public abstract void on(String name, Listener listener, boolean once);
	
	// --- UNREGISTER LISTENER ---
	
	/**
	 * Unsubscribe from an event
	 * 
	 * @param name
	 * @param listener
	 */
	public abstract void off(String name, Listener listener);
	
	// --- EMIT EVENT TO LISTENERS ---
	
	public abstract void emit(String name, Object payload);
	
}