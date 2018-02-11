/**
 * MOLECULER MICROSERVICES FRAMEWORK<br>
 * <br>
 * This project is based on the idea of Moleculer Microservices
 * Framework for NodeJS (https://moleculer.services). Special thanks to
 * the Moleculer's project owner (https://github.com/icebob) for the
 * consultations.<br>
 * <br>
 * THIS SOFTWARE IS LICENSED UNDER MIT LICENSE.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
 * <br>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:<br>
 * <br>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.<br>
 * <br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package services.moleculer.transporter.tcp;

import static services.moleculer.util.CommonUtils.getHostOrIP;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.datatree.Tree;

/**
 * Node descriptor of all (remote and local) nodes.
 */
public class NodeDescriptor {

	// --- FINAL PROPERTIES ----

	public final String nodeID;
	public final boolean local;

	protected final boolean preferHostname;

	// --- NON-FINAL PROPERTIES ----

	public volatile String host = "";
	public volatile int port;

	public volatile Tree info = new Tree();
	public volatile long seq;
	public volatile long offlineSince;

	public volatile int cpu;
	public volatile long cpuSeq;
	public volatile long cpuWhen;

	// --- LOCKS ---

	public final Lock readLock;
	public final Lock writeLock;

	// --- CONSTUCTORS ---

	public NodeDescriptor(String nodeID, boolean preferHostname, boolean local) {

		// Init final properties
		this.nodeID = nodeID;
		this.preferHostname = preferHostname;
		this.local = local;

		// Init locks
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	public NodeDescriptor(String nodeID, boolean preferHostname, String host, int port) {
		this(nodeID, preferHostname, false);

		// Set non-final properties
		this.host = host;
		this.port = port;
	}

	public NodeDescriptor(String nodeID, boolean preferHostname, boolean local, Tree info) {
		this(nodeID, preferHostname, local);

		// Set non-final properties
		host = getHostOrIP(preferHostname, info);
		port = info.get("port", 0);
		seq = info.get("seq", 0L);
	}

	// --- UPDATE CPU ---

	public void updateCpu(int cpu) {
		if (cpu >= 0 && cpu <= 100) {
			if (this.cpu != cpu) {
				this.cpu = cpu;
				cpuSeq++;
			}
			cpuWhen = System.currentTimeMillis();
		}
	}

	public void updateCpu(long cpuSeq, int cpu) {
		if (cpu >= 0 && cpu <= 100 && cpuSeq > 0 && this.cpuSeq < cpuSeq) {
			this.cpuSeq = cpuSeq;
			this.cpu = cpu;
			cpuWhen = System.currentTimeMillis();
		}
	}

	// --- MARK AS OFFLINE ---

	public boolean markAsOffline() {
		if (offlineSince == 0) {
			offlineSince = System.currentTimeMillis();
			seq++;
			info.put("seq", seq);
			return true;
		}
		return false;
	}

	public boolean markAsOffline(long seq) {
		if (this.seq < seq) {
			this.seq = seq;
			info.put("seq", seq);
			if (offlineSince == 0) {
				offlineSince = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}

	// --- MARK AS ONLINE ---

	public boolean markAsOnline(Tree info) {
		long seq = info.get("seq", 0L);
		if (this.seq < seq) {
			this.seq = seq;
			this.info = info;
			this.offlineSince = 0;
			this.host = getHostOrIP(preferHostname, info);
			this.port = info.get("port", 0);
		}
		return false;
	}

}