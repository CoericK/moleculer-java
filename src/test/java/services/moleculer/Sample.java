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
package services.moleculer;

import io.datatree.Tree;
import services.moleculer.config.ServiceBrokerConfig;
import services.moleculer.eventbus.Listener;
import services.moleculer.eventbus.Subscribe;
import services.moleculer.service.Action;
import services.moleculer.service.Name;
import services.moleculer.service.Service;
import services.moleculer.transporter.TcpTransporter;

public class Sample {

	public static void main(String[] args) throws Exception {
		System.out.println("START");
		try {

			ServiceBrokerConfig cfg = new ServiceBrokerConfig();

			TcpTransporter t = new TcpTransporter();
			t.setDebug(false);
			cfg.setTransporter(t);

			ServiceBroker broker = new ServiceBroker(cfg);

			MathService math = new MathService();
			broker.createService(math);
			broker.start();


		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("STOP");
	}

	@Name("math")
	public static class MathService extends Service {

		public Action add = ctx -> {

			// broker.getLogger().info("Call " + ctx.params);
			
			Tree res = new Tree();
			
			res.put("res", ctx.params.get("a", 0) + ctx.params.get("b", 0));
			res.put("count", ctx.params.get("count").asInteger());
			
			return res;

		};

		@Subscribe("foo.*")
		public Listener listener = payload -> {
			System.out.println("Received: " + payload);
		};

	};

}