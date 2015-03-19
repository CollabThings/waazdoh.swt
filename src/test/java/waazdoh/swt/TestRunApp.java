package waazdoh.swt;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import waazdoh.client.WClient;
import waazdoh.common.AppPreferences;
import waazdoh.common.MTimedFlag;
import waazdoh.common.WPreferences;
import waazdoh.testing.MockBeanStorage;
import waazdoh.testing.ServiceMock;
import waazdoh.testing.StaticTestPreferences;
import waazdoh.testing.TestPBinarySource;

public class TestRunApp extends TestCase {

	public void testLaunch() throws SAXException {
		String prefix = "testrunappswt";
		String username = prefix + System.currentTimeMillis();
		WPreferences p = new StaticTestPreferences(prefix, username + "@ewew");
		TestPBinarySource bsource = new TestPBinarySource(p);
		ServiceMock nservice = new ServiceMock(username, bsource);

		WClient client = new WClient(p, bsource, new MockBeanStorage(),
				nservice);

		MTimedFlag openwindowcalled = new MTimedFlag(20000);

		WSWTAppLauncher w = new WSWTAppLauncher();
		WSWTApp app = new WSWTApp() {
			WPreferences p = new AppPreferences();

			@Override
			public void openWindow() {
				openwindowcalled.trigger();
			}

			@Override
			public WPreferences getPreferences() {
				return p;
			}

			@Override
			public WClient getClient() {
				return client;
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub

			}
		};

		w.launch(app);

		openwindowcalled.waitTimer();

		assertTrue(openwindowcalled.wasTriggerCalled());
	}
}
