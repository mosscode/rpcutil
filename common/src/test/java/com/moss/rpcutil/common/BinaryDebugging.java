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
package com.moss.rpcutil.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.SerializerFactory;
import com.moss.rpcutil.common.TestHessian.Foo;
import com.moss.rpcutil.common.hessian.StandardSerializerFactory;

public class BinaryDebugging {
	
	public static void test(int length) throws Exception {
		
		byte[] data;
		{
			int dataSize = length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (int j=0; j<dataSize; j++) {
				out.write(0xFF);
			}
			data = out.toByteArray();
		}

		Foo foo = new Foo();
		foo.stuff = data;

		ByteArrayOutputStream writeOut = new ByteArrayOutputStream();
		ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		
		Hessian2Output out;
		{
			HessianDebugOutputStream db = new HessianDebugOutputStream(writeOut, new PrintWriter(logOut));
			SerializerFactory factory = new SerializerFactory();
			factory.addFactory(new StandardSerializerFactory());
			out = new Hessian2Output(db);
		}

		out.writeObject(foo);
		out.flush();
		
		String logOutString = new String(logOut.toByteArray());
		System.out.print(logOutString);
		
		if (logOutString.contains("2047L")) {
			throw new RuntimeException();
		}
		
//		System.out.println("\n" + HexUtil.toHex(o.toByteArray()));
	}

	public static void main(String[] args) throws Exception {

//		test(8131);
//		test(24758);
		
		int mb3 = 1024 * 1000 * 3;
		
		System.out.println(mb3);
		
//		test(mb3);
		
		for (int i=0; i<mb3; i++) { //3mb
			test(i);
		}
	}
}
