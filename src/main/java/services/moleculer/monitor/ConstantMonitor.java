/**
 * THIS SOFTWARE IS LICENSED UNDER MIT LICENSE.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
 * Based on Moleculer Framework for NodeJS [https://moleculer.services].
 * <br><br>
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
package services.moleculer.monitor;

import services.moleculer.service.Name;

/**
 * "Fake" system monitor, which returns a constant value.
 */
@Name("Constant-value System Monitor")
public class ConstantMonitor extends Monitor {

	// --- PROPERTIES ---

	protected int totalCpuPercent;

	// --- CONSTUCTORS ---

	public ConstantMonitor() {
	}

	public ConstantMonitor(int totalCpuPercent) {
		this.totalCpuPercent = totalCpuPercent;
	}

	// --- SYSTEM MONITORING METHODS ---

	/**
	 * Returns the system CPU usage, in percents, between 0 and 100.
	 *
	 * @return total CPU usage of the current OS
	 */
	@Override
	public int getTotalCpuPercent() {
		return totalCpuPercent;
	}

	/**
	 * Returns the system CPU usage, in percents, between 0 and 100.
	 *
	 * @return total CPU usage of the current OS
	 */
	protected int detectTotalCpuPercent() {
		return 0;
	}

	/**
	 * Returns the system CPU usage, in percents, between 0 and 100.
	 *
	 * @return total CPU usage of the current OS
	 */
	protected long detectPID() {
		return 0;
	}

	// --- GETTERS / SETTERS ---

	public void setTotalCpuPercent(int value) {
		this.totalCpuPercent = value;
	}

}