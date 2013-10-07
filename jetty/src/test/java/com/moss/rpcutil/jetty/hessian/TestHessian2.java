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

import org.joda.time.YearMonthDay;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.moss.rpcutil.jetty.SwitchingContentHandler;
import com.moss.rpcutil.proxy.ProxyFactory;
import com.moss.rpcutil.proxy.hessian.HessianProxyProvider;

public class TestHessian2 {
	
	public static final class State implements Serializable {
		YearMonthDay whenReceived;
		FakeYMD fakeYmd;
		SimpleIdentity facilitator;
		SimpleIdentity voidFacilitator;
	}
	
	public static final class FakeYMD implements Serializable {
		
		int[] iValues = new int[] { 2010, 2, 25 };
	}
	
	public static interface FooManager {
		State state();
	}
	
	public static class FooManagerImpl implements FooManager {
		
		public State state() {
			
			SimpleIdentity facilitator = new SimpleIdentity("foo@bar");
			
			State state = new State();
			state.whenReceived = new YearMonthDay();
			state.fakeYmd = new FakeYMD();
			state.facilitator = facilitator;
			state.voidFacilitator = facilitator;
			
			return state;
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
		
		manager.state();
		
		jetty.stop();
	}
	
	private static class SimpleIdentity implements Serializable  {
		
		private static final long serialVersionUID = -3645966683505451539l;

		private String name;
		
		public SimpleIdentity() {}
		
		public SimpleIdentity(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public boolean equals(Object o) {
			return
				o != null
				&&
				o instanceof SimpleIdentity
				&&
				((SimpleIdentity)o).toString().equals(toString());
		}

		public int hashCode() {
			return toString().hashCode();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
