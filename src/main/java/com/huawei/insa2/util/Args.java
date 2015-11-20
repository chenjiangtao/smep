package com.huawei.insa2.util;

import java.util.*;

public class Args {

	public static final Args EMPTY = new Args().lock();

	boolean locked;

	Map<String, Object> args;

	public Args() {
		//this(new HashMap<String, Object>());
		this(new LinkedHashMap<String, Object>());
	}

	public Args(Map<String, Object> theArgs) {
		if (theArgs == null) {
			throw new NullPointerException("argument is null");
		}
		args = theArgs;
	}

	public String get(String key, String def) {
		try {
			return args.get(key).toString();
		} catch (Exception ex) {
			return def;
		}
	}

	public int get(String key, int def) {
		try {
			return Integer.parseInt(args.get(key).toString());
		} catch (Exception ex) {
			return def;
		}
	}

	public long get(String key, long def) {
		try {
			return Long.parseLong(args.get(key).toString());
		} catch (Exception ex) {
			return def;
		}
	}

	public float get(String key, float def) {
		try {
			return Float.parseFloat(args.get(key).toString());
		} catch (Exception ex) {
			return def;
		}
	}

	public boolean get(String key, boolean def) {
		try {
			return ("true".equals(args.get(key)));
		} catch (Exception ex) {
			return def;
		}
	}

	public Object get(String key, Object def) {
		try {
			Object obj = args.get(key);
			if (obj == null) {
				return def;
			}
			return obj;
		} catch (Exception ex) {
			return def;
		}
	}

	public Args set(String key, Object value) {
		if (locked) {
			throw new UnsupportedOperationException("Args have locked,can modify");
		}
		args.put(key, value);
		return this;
	}

	public Args set(String key, int value) {
		if (locked) {
			throw new UnsupportedOperationException("Args have locked,can modify");
		}
		args.put(key, new Integer(value));
		return this;
	}

	public Args set(String key, boolean value) {
		if (locked) {
			throw new UnsupportedOperationException("Args have locked,can modify");
		}
		args.put(key, new Boolean(value));
		return this;
	}

	public Args set(String key, long value) {
		if (locked) {
			throw new UnsupportedOperationException("Args have locked,can modify");
		}
		args.put(key, new Long(value));
		return this;
	}

	public Args set(String key, float value) {
		if (locked) {
			throw new UnsupportedOperationException("Args have locked,can modify");
		}
		args.put(key, new Float(value));
		return this;
	}

	public Args set(String key, double value) {
		if (locked) {
			throw new UnsupportedOperationException("Args have locked,can modify");
		}
		args.put(key, new Double(value));
		return this;
	}

	public Args lock() {
		locked = true;
		return this;
	}

	public String toString() {
		return args.toString();
	}

	public Map<String, Object> getArgs() {
		return args;
	}
}