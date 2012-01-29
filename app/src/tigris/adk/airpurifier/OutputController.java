package tigris.adk.airpurifier;

import tigris.adk.airpurifier.R;

import android.view.ViewGroup;

public class OutputController extends AccessoryController {

	private boolean mVertical;

	OutputController(DemoKitActivity hostActivity, boolean vertical) {
		super(hostActivity);
		mVertical = vertical;
	}

	protected void onAccesssoryAttached() {
		setupRelayController(1, R.id.relay1);
		//setupRelayController(2, R.id.relay2);
	}

	private void setupRelayController(int index, int viewId) {
		RelayController r = new RelayController(mHostActivity, index,
				getResources());
		r.attachToView((ViewGroup) findViewById(viewId));
	}

}
