package waazdoh.swt;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import waazdoh.common.WLogger;

public class WSWTAppLauncher {

	private LoginWindow loginwindow;
	private WLogger log = WLogger.getLogger(this);

	public WSWTAppLauncher() {
		setProxy();
	}

	public void launch(WSWTApp app) {
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
		List<Proxy> l = null;
		try {
			l = ProxySelector.getDefault()
					.select(new URI("http://waazdoh.com"));
		} catch (URISyntaxException e) {
			log.error(e);
		}
		if (l != null) {
			for (Iterator<Proxy> iter = l.iterator(); iter.hasNext();) {
				Proxy proxy = iter.next();
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
