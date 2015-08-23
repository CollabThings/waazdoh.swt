/*******************************************************************************
 * Copyright (c) 2013 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen - initial API and implementation
 ******************************************************************************/
package waazdoh.swt;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import waazdoh.common.WLogger;
import waazdoh.common.WPreferences;

public final class AppPreferences implements WPreferences {
	private Preferences p;
	private WLogger log = WLogger.getLogger(this);

	public AppPreferences(String prefix) {
		init(prefix);
	}

	public AppPreferences() {
		init(null);
	}

	private void init(final String nprefix) {
		String propertyprefix = System.getProperty("waazdoh.prefix");
		String prefix = nprefix;
		if (prefix == null || propertyprefix != null) {
			prefix = propertyprefix;
		}

		if (prefix == null) {
			prefix = "default";
		}

		p = Preferences.userRoot().node("waazdoh/" + prefix);

		loadValues();
	}

	private void loadValues() {
		Properties values = getDefaultValues();
		if (values != null) {
			for (Object object : values.keySet()) {
				String key = "" + object;
				String value = values.getProperty(key);
				p.put(key, value);
			}
		}
	}

	private Properties getDefaultValues() {
		Properties properties = new Properties();
		try {
			InputStream is = ClassLoader
					.getSystemResourceAsStream("properties.ini");
			if (is != null) {
				properties.load(is);
			} else {
				properties.load(new FileReader("properties.ini"));
			}
		} catch (IOException e) {
			WLogger.getLogger(this).error(e);
		}

		return properties;
	}

	public Set<String> getNames() {
		Set<String> ret = new HashSet<String>();
		String[] keys;
		try {
			keys = p.keys();
			for (final String string : keys) {
				ret.add(string);
			}
			return ret;
		} catch (BackingStoreException e) {
			log.error(e);
			return null;
		}
	}

	public String get(final String name, final String ndefaultvalue) {
		String defaultvalue = ndefaultvalue;

		if (p.get(name, null) == null && ndefaultvalue != null) {
			if (System.getProperty("waazdoh." + name) != null) {
				defaultvalue = System.getProperty("waazdoh." + name);
			}
			//
			set(name, defaultvalue);
		}
		String parsed = parse(name, p.get(name, defaultvalue));

		log.info("get " + name + " = " + parsed);
		return parsed;
	}

	public int getInteger(final String string, int i) {
		String sint = get(string, "" + i);
		return Integer.parseInt(sint);
	}

	private String parse(final String name, final String value) {
		String returnvalue = value;
		if (name.indexOf(".home.") > 0 && value != null
				&& value.indexOf("/") != 0) {
			returnvalue = System.getProperty("user.home") + File.separator
					+ value;

		}
		return returnvalue;
	}

	public void set(final String name, String value) {
		if (name != null && value != null) {
			p.put(name, value);
		}
	}

	public void set(final String name, boolean b) {
		set(name, "" + b);
	}

	public boolean getBoolean(final String valuename, boolean defaultvalue) {
		return "true".equals("" + get(valuename, "" + defaultvalue));
	}

	public double getDouble(String string, double d) {
		String sdouble = get(string, "" + d);
		if (sdouble != null) {
			return Double.parseDouble(sdouble);
		} else {
			return 0;
		}
	}
}
