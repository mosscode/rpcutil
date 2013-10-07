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
package com.moss.rpcutil.proxy.hessian;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.SerializerFactory;
import com.moss.rpcutil.common.hessian.StandardSerializerFactory;
import com.moss.rpcutil.proxy.ProxyProvider;

public final class HessianProxyProvider implements ProxyProvider {
	
	private final HessianProxyFactory factory;

	public HessianProxyProvider() {
		SerializerFactory serial = new SerializerFactory();
		serial.addFactory(new StandardSerializerFactory());
		
		factory = new HessianProxyFactory();
		factory.setSerializerFactory(serial);
	}

    public HessianProxyProvider setAllowNonSerializable(boolean b) {
        factory.getSerializerFactory().setAllowNonSerializable(b);
        return this;
    }
	
	public HessianProxyProvider add(AbstractSerializerFactory factory) {
		this.factory.getSerializerFactory().addFactory(factory);
		return this;
	}

	public <T> T getProxy(Class<T> iface, String url) {
		try {
			T proxy = (T) factory.create(iface, url);
			return proxy;
		}
		catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
