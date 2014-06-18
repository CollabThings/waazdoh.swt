package waazdoh.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import waazdoh.client.WClientAppLogin;
import waazdoh.util.MLogger;
import waazdoh.util.MPreferences;

public class LoginWindow {

	protected Shell shell;
	private WSWTApp app;
	private WClientAppLogin applogin;
	private MLogger log = MLogger.getLogger(this);
	private Link link;
	private Browser browser;

	public LoginWindow(WSWTApp app) {
		this.app = app;
	}

	private void loginLinkSelected() {
		log.info("You have selected: " + getURL());
		// Open default external browser
		org.eclipse.swt.program.Program.launch(getURL());
	}

	private String getURL() {
		return getApplogin().getURL();
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		int w = 497;
		int h = 329;
		shell.setSize(w, h);
		shell.setText("Login");

		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);
		shell.setLayout(new GridLayout(1, false));

		link = new Link(shell, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				loginLinkSelected();
			}
		});
		link.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		link.setText("Working on it...");
		browser = new Browser(shell, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		//
		startLoginCheck();
		waitForLogin();
	}

	private void waitForLogin() {
		shell.getDisplay().timerExec(1000, new Runnable() {

			@Override
			public void run() {
				try {
					if (app.getClient().getService().isLoggedIn()) {
						shell.dispose();
					} else {
						waitForLogin();
					}
				} catch (Exception e) {
					log.error(e);
					waitForLogin();
				}
			}
		});
	}

	private void startLoginCheck() {
		final Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				if (!loginWithStored()) {
					synchronized (app) {
						try {
							while (getApplogin().getSessionId() == null
									&& !shell.isDisposed()) {
								applogin = app.getClient().checkAppLogin(
										getApplogin().getId());
								app.wait(2000);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (app.getClient().getService().isLoggedIn()) {
						app.getPreferences().set(
								MPreferences.PREFERENCES_SESSION,
								applogin.getSessionId());
					}
				}
				//
				dispose();
			}
		});
		t.start();
	}

	public boolean loginWithStored() {
		String session = app.getPreferences().get(
				MPreferences.PREFERENCES_SESSION, "unknown_session");

		try {
			return app.getClient().setSession(session);
		} catch (Exception e) {
			log.info("failed at login " + e);
			return false;
		}
	}

	public void dispose() {
		if (!shell.isDisposed()) {
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					shell.dispose();
				}
			});
		}
	}

	public WClientAppLogin getApplogin() {
		if (applogin == null) {
			this.applogin = app.getClient().requestAppLogin();
			shell.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					link.setText("<a href=\"" + getURL()
							+ "\">Open in a browser</a>");

					String url = "" + getURL() + "?simplepage=true";
					log.info("opening url " + url);
					browser.setUrl(url);
				}
			});
		}
		//
		return applogin;
	}
}
