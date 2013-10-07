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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public final class BSHttpExchange extends HttpExchange
{

    private HttpContext _context;

    private HttpServletRequest _req;

    private HttpServletResponse _resp;

    private Headers _responseHeaders = new Headers();

    private int _responseCode = 0;

    private InputStream _is;

    private OutputStream _os;

    private HttpPrincipal _principal;


    public BSHttpExchange(HttpContext context, HttpServletRequest req,HttpServletResponse resp)
    {
        this._context = context;
        this._req = req;
        this._resp = resp;
        try
        {
            this._is = req.getInputStream();
            this._os = resp.getOutputStream();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Headers getRequestHeaders()
    {
        Headers headers = new Headers();
        Enumeration en = _req.getHeaderNames();
        while (en.hasMoreElements())
        {
            String name = (String) en.nextElement();
            Enumeration en2 = _req.getHeaders(name);
            while (en2.hasMoreElements())
            {
                String value = (String) en2.nextElement();
                headers.add(name, value);
            }
        }
        return headers;
    }

    @Override
    public Headers getResponseHeaders()
    {
        return _responseHeaders;
    }

    @Override
    public URI getRequestURI()
    {
        try
        {
            String uriAsString = _req.getRequestURI();
            if (_req.getQueryString() != null)
                uriAsString += "?" + _req.getQueryString();

            return new URI(uriAsString);
        }
        catch (URISyntaxException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getRequestMethod()
    {
        return _req.getMethod();
    }

    @Override
    public HttpContext getHttpContext()
    {
        return _context;
    }

    @Override
    public void close()
    {
        try
        {
            _resp.getOutputStream().close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InputStream getRequestBody()
    {
        return _is;
    }

    @Override
    public OutputStream getResponseBody()
    {
        return _os;
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength)
    throws IOException
    {
        this._responseCode = rCode;

        Iterator it = _responseHeaders.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            List values = (List) entry.getValue();
            for (int i = 0; i < values.size(); i++)
            {
                String value = (String) values.get(i);
                _resp.setHeader(name, value);
            }
        }
        if (responseLength > 0)
            _resp.setHeader("content-length", "" + responseLength);
        _resp.setStatus(rCode);
    }

    @Override
    public InetSocketAddress getRemoteAddress()
    {
        return new InetSocketAddress(_req.getRemoteAddr(), _req.getRemotePort());
    }

    @Override
    public int getResponseCode()
    {
        return _responseCode;
    }

    @Override
    public InetSocketAddress getLocalAddress()
    {
        return new InetSocketAddress(_req.getLocalAddr(), _req.getLocalPort());
    }

    @Override
    public String getProtocol()
    {
        return _req.getProtocol();
    }

    @Override
    public Object getAttribute(String name)
    {
        return _req.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value)
    {
        _req.setAttribute(name, value);
    }

    @Override
    public void setStreams(InputStream i, OutputStream o)
    {
        _is = i;
        _os = o;
    }

    @Override
    public HttpPrincipal getPrincipal()
    {
        return _principal;
    }

    public void setPrincipal(HttpPrincipal principal)
    {
        this._principal = principal;
    }
}
