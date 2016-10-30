package waazdoh.swt;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import waazdoh.client.WClient;
import waazdoh.common.WLogger;
import waazdoh.common.vo.AppLoginVO;

public class LoginWindow {
	protected Shell shell;
	private WSWTApp app;
	private AppLoginVO applogin;
	private WLogger log = WLogger.getLogger(this);
	private Link link;
	private Text text;

	public LoginWindow(WSWTApp app) {
		this.app = app;
	}

	private void loginLinkSelected() {
		log.info("You have selected: " + getURL());
		// Open default external browser
		org.eclipse.swt.program.Program.launch(getURL());
	}

	private String getURL() {
		return getApplogin().getUrl();
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

		Label lblTitle = new Label(shell, SWT.NONE);
		lblTitle.setAlignment(SWT.CENTER);
		lblTitle.setText("Click the link or copy URL to accept application");

		link = new Link(shell, SWT.NONE);
		link.addSelectionListener(new CTSelectionAdapter(e -> loginLinkSelected()));

		link.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		link.setText("Working on it...");

		text = new Text(shell, SWT.BORDER | SWT.MULTI);
		GridData gdtext = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdtext.heightHint = 102;
		text.setLayoutData(gdtext);
		//
		startLoginCheck();
		waitForLogin();
	}

	private void waitForLogin() {
		shell.getDisplay().timerExec(1000, () -> {

			try {
				if (app.getClient().isLoggedIn()) {
					shell.dispose();
				} else {
					waitForLogin();
				}
			} catch (Exception e) {
				log.error(e);
				waitForLogin();
			}
		});
	}

	private void startLoginCheck() {
		final Thread t = new Thread(() -> {
			if (!app.getClient().trySavedSession()) {
				WClient client = app.getClient();
				synchronized (app) {
					loop(client);
				}
			}
			//
			dispose();
		});
		t.start();
	}

	private void loop(WClient client) {
		while (!shell.isDisposed() && (applogin == null || applogin.getSessionid() == null)) {
			try {
				String id = getApplogin().getId();
				client.checkAppLogin(id);
				waitApp();
			} catch (Exception e) {
				log.error(e);
				waitApp();
			}
		}
	}

	private void waitApp() {
		synchronized (app) {
			try {
				app.wait(2000);
			} catch (InterruptedException e) {
				log.error(e);
				Thread.currentThread().interrupt();
			}
		}
	}

	public void dispose() {
		if (!shell.isDisposed()) {
			shell.getDisplay().asyncExec(() -> shell.dispose());
		}
	}

	public AppLoginVO getApplogin() {
		if (applogin == null) {
			log.info("creating applogin " + app);
			log.info("client:" + app.getClient());
			this.applogin = app.getClient().requestAppLogin();
			shell.getDisplay().asyncExec(() -> {
				if (!link.isDisposed()) {
					link.setText("<a href=\"" + getURL() + "\">Open in a browser</a>");

					String url = "" + getURL() + applogin.getId();
					log.info("opening url " + url);
					text.setText(url);
					//
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (IOException | URISyntaxException e) {
						log.error("getAppLogin failed to open browser " + url);
						log.error(e);
					}
				} else {
					log.info("Widget disposed");
				}
			});
		}
		return applogin;
	}
}
