/**
 * This software is licensed under MIT license.<br>
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
package services.moleculer.config;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.datatree.Tree;
import services.moleculer.ServiceBroker;
import services.moleculer.service.Name;
import services.moleculer.service.Service;
/**
 * Spring-based Component Registry. The Spring Framework provides a
 * comprehensive programming and configuration model for modern Java-based
 * enterprise applications - on any kind of deployment platform
 * (https://projects.spring.io/spring-framework/). Minimalistic Spring config:
 * <br>
 * <br>
 * &lt;beans ...&gt;
 * <ul>
 * &lt;context:component-scan base-package="your.service.package" /&gt;<br>
 * &lt;bean id="components"
 * class="services.moleculer.config.SpringComponentRegistry" /&gt;<br>
 * &lt;bean id="settings"
 * class="services.moleculer.config.ServiceBrokerSettings"&gt;
 * <ul>
 * &lt;property name="nodeID" value="server-2" /&gt;<br>
 * &lt;property name="component" ref="components"/&gt;
 * </ul>
 * &lt;/bean&gt;<br>
 * &lt;bean id="broker" class="services.moleculer.ServiceBroker"
 * init-method="start" destroy-method="stop"&gt;
 * <ul>
 * &lt;constructor-arg ref="settings"/&gt;
 * </ul>
 * &lt;/bean&gt;<br>
 * </ul>
 * &lt;/beans&gt;
 * 
 * @see StandaloneComponentRegistry
 * @see GuiceComponentRegistry
 */
@Name("Spring Component Registry")
public final class SpringComponentRegistry extends BaseComponentRegistry implements ApplicationContextAware {

	// --- FIND COMPONENTS AND SERVICES ---

	/**
	 * Pointer to Spring Application Context
	 */
	private ApplicationContext ctx;

	@Override
	public final void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}

	@Override
	protected final void findServices(ServiceBroker broker, Tree config) throws Exception {

		// Find Moleculer Services in Spring Application Context
		Map<String, MoleculerComponent> map = ctx.getBeansOfType(MoleculerComponent.class);
		for (Map.Entry<String, MoleculerComponent> entry : map.entrySet()) {
			MoleculerComponent component = entry.getValue();
			if (isInternalComponent(component) || component instanceof Service) {
				continue;
			}
			String name = entry.getKey();
			componentMap.put(name, new MoleculerComponentContainer(component, configOf(name, config)));
			logger.info("Spring Bean \"" + name + "\" registered as Moleculer Component.");
		}

		// Find Moleculer Components (eg. DAO classes) in Spring Application Context
		Map<String, Service> serviceMap = ctx.getBeansOfType(Service.class);
		for (Map.Entry<String, Service> entry : serviceMap.entrySet()) {
			Service service = entry.getValue();
			String name = service.name();
			broker.createService(service, configOf(name, config));
			logger.info("Spring Bean \"" + name + "\" registered as Moleculer Service.");
		}
	}

}