package waazdoh.swt;

import waazdoh.client.WClient;
import waazdoh.common.WPreferences;

public interface WSWTApp {

	WClient getClient();

	WPreferences getPreferences();

	void close();

	void openWindow();

}
