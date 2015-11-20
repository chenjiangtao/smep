package com.huawei.insa2.util;

import java.io.*;
import java.util.*;

public class Resource {

	@SuppressWarnings("unused")
	private Cfg resource;

	public Resource(String url) throws IOException {
		// init(url);
	}

	public Resource(Class<?> c, String url) throws IOException {

		String className = c.getName();

		int i = className.lastIndexOf('.');
		if (i > 0) {
			className = className.substring(i + 1);
		}
		// URL u = new URL(c.getResource(className+".class"),url);
		// init(u.toString());
	}

	public void init(String url) throws IOException {
		String str = url + '_' + Locale.getDefault();
		// InputStream in = null;
		int i;
		for (;;) {
			try {
				resource = new Cfg(str + ".xml", false);
				return;
			} catch (IOException ex) {
				i = str.lastIndexOf('_');
				if (i < 0) {
					throw new MissingResourceException(
							"Can't find resource url:" + url + ".xml",
							getClass().getName(), null);
				}
				str = str.substring(0, i);
				continue;
			}
		}

	}

	public String get(String key) {
		return key;
	}

}
