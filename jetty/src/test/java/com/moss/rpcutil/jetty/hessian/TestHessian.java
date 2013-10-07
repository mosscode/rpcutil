/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of rpcutil.
 *
 * rpcutil is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * rpcutil is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rpcutil; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.rpcutil.jetty.hessian;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.YearMonthDay;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.moss.rpcutil.jetty.SwitchingContentHandler;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

public class TestHessian {
	
	public static class Foo implements Serializable {
		
		private UUID id;
		private URL location;
		private BigDecimal amount;

		public URL getLocation() {
			return location;
		}

		public void setLocation(URL location) {
			this.location = location;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}
	}
	
	public static interface FooManager {
		Foo foo(Foo foo);
	}
	
	public static class FooManagerImpl implements FooManager {
		public Foo foo(Foo foo) {
			System.out.println(foo.getId());
			System.out.println(foo.getLocation());
			System.out.println(foo.getAmount());
			return foo;
		}
	}

	@Test
	public void test() throws Exception {
		
		HessianContentHandler hessian = new HessianContentHandler(FooManager.class, new FooManagerImpl());
		
		SwitchingContentHandler handler = new SwitchingContentHandler("/");
		handler.addHandler(hessian);

		Server jetty = new Server(8765);
		jetty.addHandler(handler);
		jetty.start();
		
		ProxyFactory factory = new ProxyFactory(new HessianProxyProvider());
		FooManager manager = factory.create(FooManager.class, "http://localhost:8765/");
		
		Foo foo = new Foo();
		foo.setId(UUID.randomUUID());
		foo.setLocation(new URL("http://localhost:9999"));
		foo.setAmount(BigDecimal.TEN);
		
		manager.foo(foo);
		
		jetty.stop();
	}
}
