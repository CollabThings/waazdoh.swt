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
import java.util.Properties;
import java.util.Set;

import waazdoh.common.WLogger;
import waazdoh.common.WPreferences;
import waazdoh.common.util.PropertiesPreferences;

public final class AppPreferences implements WPreferences {
	private PropertiesPreferences p;
	private WLogger log = WLogger.getLogger(this);
	private Properties defaults;

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
			prefix = "waazdoh/default";
		}

		p = new PropertiesPreferences(prefix);
		defaults = getDefaultValues();
	}

	private Properties getDefaultValues() {
		Properties properties = new Properties();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("properties.ini");
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
		return p.getNames();
	}

	public String get(final String name, final String ndefaultvalue) {
		String defaultvalue = ndefaultvalue;
		if (defaults.get(name) != null) {
			defaultvalue = "" + defaults.get(name);
		}

		if (System.getProperty("waazdoh." + name) != null) {
			defaultvalue = System.getProperty("waazdoh." + name);
		}

		String parsed = parse(name, p.get(name, defaultvalue));

		log.debug("get " + name + " = " + parsed);
		return parsed;
	}

	public int getInteger(final String string, int i) {
		String sint = get(string, "" + i);
		return Integer.parseInt(sint);
	}

	private String parse(final String name, final String value) {
		String returnvalue = value;
		if (name.indexOf(".home.") > 0 && value != null && value.indexOf("/") != 0) {
			returnvalue = System.getProperty("user.home") + File.separator + value;

		}
		return returnvalue;
	}

	public void set(final String name, String value) {
		p.set(name, value);
	}

	public void set(final String name, boolean b) {
		set(name, "" + b);
	}

	public boolean getBoolean(final String valuename, boolean defaultvalue) {
		return p.getBoolean(valuename, defaultvalue);
	}

	public double getDouble(String string, double d) {
		return p.getDouble(string, d);
	}
}
