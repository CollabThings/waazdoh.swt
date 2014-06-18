package waazdoh.swt;

import waazdoh.client.WClient;
import waazdoh.util.MPreferences;

public interface WSWTApp {

	WClient getClient();

	MPreferences getPreferences();

	void close();

	void openWindow();

}
