package com.yc.qa.as400.util;

import com.esotericsoftware.reflectasm.ConstructorAccess;

/**
 *
 * @author limit (Yurii Chukhrai)
 */
public class ObjectSupplier {

	public static synchronized <T> T $(Class<T> pageObject) {
		return ConstructorAccess.get(pageObject).newInstance();
	}

	private ObjectSupplier() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}
}
