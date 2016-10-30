package waazdoh.swt;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CTSelectionAdapter extends SelectionAdapter {

	private ISelected iselected;

	public CTSelectionAdapter(ISelected i) {
		this.iselected = i;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		iselected.selected(e);
	}

	public interface ISelected {
		void selected(SelectionEvent e);
	}
}
