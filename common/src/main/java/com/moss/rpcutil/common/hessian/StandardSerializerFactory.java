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
package com.moss.rpcutil.common.hessian;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URL;
import java.util.UUID;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.AbstractStringValueDeserializer;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.StringValueDeserializer;
import com.caucho.hessian.io.StringValueSerializer;

public final class StandardSerializerFactory extends AbstractSerializerFactory {

	private final Deserializer urlDeserializer;
	private final Deserializer bigDecimalDeserializer;
	private final Deserializer uuidDeserializer;
	private final Deserializer yearMonthDayDeserializer;
	
	public StandardSerializerFactory() {
		urlDeserializer = new StringValueDeserializer(URL.class);
		bigDecimalDeserializer = new StringValueDeserializer(BigDecimal.class);
		uuidDeserializer = new UUIDDeserializer();
		yearMonthDayDeserializer = new YearMonthDayDeserializer();
	}

	@Override
	public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
		if (URL.class.equals(cl)) {
			return urlDeserializer;
		}
		else if (BigDecimal.class.equals(cl)) {
			return bigDecimalDeserializer;
		}
		else if (UUID.class.equals(cl)) {
			return uuidDeserializer;
		}
		else if (cl.getName().equals("org.joda.time.YearMonthDay")) {
			return yearMonthDayDeserializer;
		}
		return null;
	}

	@Override
	public Serializer getSerializer(Class cl) throws HessianProtocolException {
		if (URL.class.equals(cl)) {
			return StringValueSerializer.SER;
		}
		else if (BigDecimal.class.equals(cl)) {
			return StringValueSerializer.SER;
		}
		else if (UUID.class.equals(cl)) {
			return StringValueSerializer.SER;
		}
		else if (cl.getName().equals("org.joda.time.YearMonthDay")) {
			return StringValueSerializer.SER;
		}
		return null;
	}
	
	private final class UUIDDeserializer extends AbstractStringValueDeserializer {
		
		@Override
		public Class<?> getType() {
			return UUID.class;
		}
		
		@Override
		protected Object create(String value) throws IOException {
			if (value == null) {
				throw new RuntimeException();
			}
			else {
				UUID uuid = UUID.fromString(value);
				return uuid;
			}
		}
	}
	
	private final class YearMonthDayDeserializer extends AbstractStringValueDeserializer {
		
		@Override
		public Class<?> getType() {
			return Object.class;
		}
		
		@Override
		protected Object create(String value) throws IOException {
			if (value == null) {
				throw new RuntimeException();
			}
			else {
				try {
					Class cl = Class.forName("org.joda.time.YearMonthDay");
					Constructor con = cl.getConstructor(Object.class);
					Object o = con.newInstance(value);
					return o;
				}
				catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
}
