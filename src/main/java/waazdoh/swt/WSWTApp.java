package waazdoh.swt;

import waazdoh.client.WClient;
import waazdoh.cutils.MPreferences;

public interface WSWTApp {

	WClient getClient();

	MPreferences getPreferences();

}
