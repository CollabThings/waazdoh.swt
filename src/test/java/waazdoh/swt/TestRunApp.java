package waazdoh.swt;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import waazdoh.client.WClient;
import waazdoh.client.storage.local.FileBeanStorage;
import waazdoh.common.MTimedFlag;
import waazdoh.common.WPreferences;
import waazdoh.common.client.MemoryBeanStorage;
import waazdoh.common.testing.StaticService;
import waazdoh.common.testing.StaticTestPreferences;
import waazdoh.cp2p.P2PBinarySource;

public class TestRunApp extends TestCase {
	private String prefix = "testrunappswt";
	private StaticService nservice;

	private final class WSWTAppImplementation implements WSWTApp {
		WPreferences p = new AppPreferences();
		private MTimedFlag openwindowcalled;
		private WClient client;
		private boolean closed;

		public WSWTAppImplementation(MTimedFlag openwindowcalled, WClient client) {
			this.openwindowcalled = openwindowcalled;
			this.client = client;
		}

		@Override
		public void openWindow() {
			openwindowcalled.trigger();
		}

		@Override
		public boolean isClosed() {
			return this.closed;
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
			this.closed = true;
		}
	}

	public void testLaunch() throws SAXException {
		WClient client = getNewClient();

		MTimedFlag openwindowcalled = new MTimedFlag(20000);

		WSWTAppLauncher w = new WSWTAppLauncher();
		WSWTApp app = new WSWTAppImplementation(openwindowcalled, client);

		w.launch(app);

		openwindowcalled.waitTimer();

		assertTrue(openwindowcalled.wasTriggerCalled());
	}

	public void testAppLogin() {
		WClient client = getNewClient();

		MTimedFlag openwindowcalled = new MTimedFlag(20000);

		WSWTAppLauncher w = new WSWTAppLauncher();
		WSWTApp app = new WSWTAppImplementation(openwindowcalled, client);

		w.launch(app);

		openwindowcalled.waitTimer();

		assertTrue(openwindowcalled.wasTriggerCalled());
}
	
	private WPreferences getPreferences(String username) {
		WPreferences p = new StaticTestPreferences(prefix, username + "@ewew");
		return p;
	}

	public void testFailLaunch() throws SAXException {
		WClient client = getNewClient();
		client.stop();

		MTimedFlag openwindowcalled = new MTimedFlag(20000);

		WSWTAppLauncher w = new WSWTAppLauncher();
		WSWTApp app = new WSWTAppImplementation(openwindowcalled, client);

		w.launch(app);

		assertTrue(app.isClosed());
	}

	private WClient getNewClient() {
		String username = prefix + System.currentTimeMillis();
		WPreferences p = getPreferences(username);
		P2PBinarySource bsource = getBinarySource(p);
		nservice = new StaticService(username);
		
		WClient client = new WClient(p, bsource, new MemoryBeanStorage(),
				nservice);
		return client;
	}

	private P2PBinarySource getBinarySource(WPreferences p) {
		P2PBinarySource bsource = new P2PBinarySource(p,
				new FileBeanStorage(p), false);
		return bsource;
	}

}
