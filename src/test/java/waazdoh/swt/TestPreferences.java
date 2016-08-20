package waazdoh.swt;

import junit.framework.TestCase;

public class TestPreferences extends TestCase {

	public void testGet() {
		AppPreferences p = getPreferences();
		assertEquals("testrandom", p.get("testrandom", "testrandom"));
	}

	public void disableTestGetNames() {
		AppPreferences p = getPreferences();
		int size = p.getNames().size();
		assertTrue("size " + size, size == 1);

		assertNotNull(p.get("testadd", "testadd"));

		assertEquals(size + 1, p.getNames().size());
	}

	public void testBoolean() {
		AppPreferences p = getPreferences();
		p.set("booleantest", true);
		assertTrue(p.getBoolean("booleantest", false));
		assertFalse(p.getBoolean("booleantestfalse", false));
	}

	public void testInteger() {
		AppPreferences p = getPreferences();
		p.set("inttest", "" + 0);
		assertEquals(0, p.getInteger("inttest", 1));
		assertEquals(2, p.getInteger("inttest2", 2));
	}

	public void testDouble() {
		AppPreferences p = getPreferences();
		p.set("doubletest", "" + 0.1);
		assertEquals(0.1, p.getDouble("doubletest", 2.1));
		assertEquals(0.2, p.getDouble("doubletest2", 0.2));

		p.set("doubletest3", "FAIL");
		try {
			p.getDouble("doubletest3", 3);
			assertTrue(false);
		} catch (NumberFormatException e) {
			assertNotNull(e);
		}
	}

	private AppPreferences getPreferences() {
		AppPreferences p = new AppPreferences("" + Math.random()
				+ System.currentTimeMillis());
		return p;
	}

}
