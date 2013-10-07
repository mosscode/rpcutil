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
package com.moss.rpcutil.jetty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpStatus;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

public final class SwitchingContentHandler extends AbstractHandler {

    public static SwitchingContentHandler create(String[] targets) {
        return new SwitchingContentHandler(targets);
    }
	
	private final String[] targets;
	private final List<ContentHandler> handlers = new ArrayList<ContentHandler>();
	
	public SwitchingContentHandler(String...targets) {
		this.targets = Arrays.copyOf(targets, targets.length);
	}

	public SwitchingContentHandler addHandler(ContentHandler handler) {
		handlers.add(handler);
		return this;
	}
	
	public void handle(String target, HttpServletRequest req, HttpServletResponse res, int dispatch) throws IOException, ServletException {
		
		boolean targetMatched = false;
		for (String t : targets) {
			if (target.startsWith(t)) {
				targetMatched = true;
				break;
			}
		}
		
		if (!targetMatched) {
			return;
		}
		
		((Request)req).setHandled(true);
		
		boolean handled = false;
		for (ContentHandler h : handlers) {
			handled = h.handle(target, req, res);
			if (handled) {
				break;
			}
		}
		
		if (!handled) {
			res.setStatus(HttpStatus.ORDINAL_415_Unsupported_Media_Type);
		}
	}
}
