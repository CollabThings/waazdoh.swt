package waazdoh.swt;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import waazdoh.client.WClient;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.common.MTimedFlag;
import waazdoh.common.WPreferences;
import waazdoh.common.client.MemoryBeanStorage;
import waazdoh.cp2p.P2PBinarySource;
import waazdoh.testing.StaticService;
import waazdoh.testing.StaticTestPreferences;

public class TestRunApp extends TestCase {

	public void testLaunch() throws SAXException {
		String prefix = "testrunappswt";
		String username = prefix + System.currentTimeMillis();
		WPreferences p = new StaticTestPreferences(prefix, username + "@ewew");
		P2PBinarySource bsource = new P2PBinarySource(p,
				new FileBeanStorage(p), false);
		StaticService nservice = new StaticService(username);

		WClient client = new WClient(p, bsource, new MemoryBeanStorage(),
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
