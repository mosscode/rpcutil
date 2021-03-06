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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.ServiceContext;
import com.moss.rpcutil.common.hessian.StandardSerializerFactory;
import com.moss.rpcutil.jetty.ContentHandler;

public final class HessianContentHandler implements ContentHandler {
	
	private final SerializerFactory serializerFactory;
	private final HessianSkeleton skeleton;
	
	public HessianContentHandler(Class iface, Object impl, AbstractSerializerFactory...factories) {
		this.serializerFactory = new SerializerFactory();
		serializerFactory.addFactory(new StandardSerializerFactory());
		for (AbstractSerializerFactory f : factories) {
			serializerFactory.addFactory(f);
		}
		this.skeleton = new HessianSkeleton(impl, iface);
	}

    public HessianContentHandler setAllowNonSerializable(boolean b) {
        serializerFactory.setAllowNonSerializable(b);
        return this;
    }
	
	public boolean handle(String target, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		
		String contentType = req.getContentType();
		
		boolean handle;
		if ("x-application/hessian".equals(contentType)) {
			handle = true;
		}
		else {
			handle = false;
		}

		if (handle) {

			if (! req.getMethod().equals("POST")) {
				res.setStatus(500, "Hessian Requires POST");
				PrintWriter out = res.getWriter();

				res.setContentType("text/html");
				out.println("<h1>Hessian Requires POST</h1>");

				return handle;
			}

			String serviceId = req.getPathInfo();
			String objectId = req.getParameter("id");
			if (objectId == null)
				objectId = req.getParameter("ejbid");

			ServiceContext.begin(req, serviceId, objectId);

			try {
				InputStream is = req.getInputStream();
				OutputStream os = res.getOutputStream();

				res.setContentType("application/x-hessian");

				skeleton.invoke(is, os, serializerFactory);
			} catch (RuntimeException e) {
				throw e;
			} catch (ServletException e) {
				throw e;
			} catch (Throwable e) {
				throw new ServletException(e);
			} finally {
				ServiceContext.end();
			}
		}

		return handle;
	}
}
