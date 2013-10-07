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
package com.moss.rpcutil.jetty.jaxws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class BSHttpContext extends HttpContext {
	
	final Map<String, Object> attributes = new HashMap<String, Object>();
	final List<Filter> filters = new ArrayList<Filter>();
	Authenticator authenticator = null;
	HttpHandler handler;
	
	public BSHttpContext() {}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	@Override
	public List<Filter> getFilters() {
		return filters;
	}

	@Override
	public HttpHandler getHandler() {
		return handler;
	}

	@Override
	public String getPath() {
		throw new RuntimeException("No such thing!"); 
	}

	@Override
	public HttpServer getServer() {
		throw new RuntimeException("No such thing!");
	}

	@Override
	public Authenticator setAuthenticator(Authenticator arg0) {
		return null;
	}

	@Override
	public void setHandler(HttpHandler arg0) {
		this.handler = arg0;
	}
}
