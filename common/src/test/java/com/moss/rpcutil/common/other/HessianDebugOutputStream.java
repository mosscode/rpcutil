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
package com.moss.rpcutil.common.other;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Debugging output stream for Hessian requests.
 */
class HessianDebugOutputStream extends OutputStream
{
  private OutputStream _os;
  
  private HessianDebugState _state;
  
  /**
   * Creates an uninitialized Hessian input stream.
   */
  public HessianDebugOutputStream(OutputStream os, PrintWriter dbg)
  {
    _os = os;

    _state = new HessianDebugState(dbg);
  }
  
  /**
   * Creates an uninitialized Hessian input stream.
   */
  public HessianDebugOutputStream(OutputStream os, Logger log, Level level)
  {
    this(os, new PrintWriter(new LogWriter(log, level)));
  }

  public void startTop2()
  {
    _state.startTop2();
  }

  public void startStreaming()
  {
    _state.startStreaming();
  }

  /**
   * Writes a character.
   */
  public void write(int ch)
    throws IOException
  {
    ch = ch & 0xff;
    
    _os.write(ch);

    _state.next(ch);
  }

  public void flush()
    throws IOException
  {
    _os.flush();
  }

  /**
   * closes the stream.
   */
  public void close()
    throws IOException
  {
    OutputStream os = _os;
    _os = null;

    if (os != null) {
      _state.next(-1);
      os.close();
    }

    _state.println();
  }

  static class LogWriter extends Writer {
    private Logger _log;
    private Level _level;
    private StringBuilder _sb = new StringBuilder();

    LogWriter(Logger log, Level level)
    {
      _log = log;
      _level = level;
    }

    public void write(char ch)
    {
      if (ch == '\n' && _sb.length() > 0) {
	_log.log(_level, _sb.toString());
	_sb.setLength(0);
      }
      else
	_sb.append((char) ch);
    }

    public void write(char []buffer, int offset, int length)
    {
      for (int i = 0; i < length; i++) {
	char ch = buffer[offset + i];
	
	if (ch == '\n' && _sb.length() > 0) {
	  _log.log(_level, _sb.toString());
	  _sb.setLength(0);
	}
	else
	  _sb.append((char) ch);
      }
    }

    public void flush()
    {
    }

    public void close()
    {
    }
  }
}
