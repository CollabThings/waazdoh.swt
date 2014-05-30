package waazdoh.swt;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import waazdoh.cutils.MLogger;

public class WSWTAppLauncher {

	private WSWTApp app;
	private LoginWindow loginwindow;
	private MLogger log = MLogger.getLogger(this);

	public WSWTAppLauncher() {
		setProxy();
	}

	public void launch(WSWTApp app) {
		this.app = app;
		try {
			loginwindow = new LoginWindow(app);
			loginwindow.open();

			if (!app.getClient().isRunning()) {
				app.close();
			} else {
				app.openWindow();
			}
		} finally {
			app.close();
		}
	}

	private void setProxy() {
		System.setProperty("java.net.useSystemProxies", "true");
		log.info("detecting proxies");
		List l = null;
		try {
			l = ProxySelector.getDefault()
					.select(new URI("http://waazdoh.com"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				java.net.Proxy proxy = (java.net.Proxy) iter.next();
				log.info("proxy hostname : " + proxy.type());

				InetSocketAddress addr = (InetSocketAddress) proxy.address();

				if (addr == null) {
					log.info("No Proxy");
				} else {
					log.info("proxy hostname : " + addr.getHostName());
					System.setProperty("http.proxyHost", addr.getHostName());
					log.info("proxy port : " + addr.getPort());
					System.setProperty("http.proxyPort",
							Integer.toString(addr.getPort()));
				}
			}
		}
	}

}
