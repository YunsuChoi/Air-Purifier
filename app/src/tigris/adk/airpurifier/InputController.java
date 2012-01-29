package tigris.adk.airpurifier;

import java.text.DecimalFormat;
import java.util.ArrayList;

import tigris.adk.airpurifier.R;


import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class InputController extends AccessoryController {
	private TextView mTemperature;
	private TextView mLightView;
	//private TextView mLightRawView;
	
	ArrayList<SwitchDisplayer> mSwitchDisplayers;
	private final DecimalFormat mLightValueFormatter = new DecimalFormat(
			"###" + (char)0x0025);
	private final DecimalFormat mTemperatureFormatter = new DecimalFormat(
			"###" + (char)0x0025);

	InputController(DemoKitActivity hostActivity) {
		super(hostActivity);
		mTemperature = (TextView) findViewById(R.id.tempValue);
		mLightView = (TextView) findViewById(R.id.lightPercentValue);
		//mLightRawView = (TextView) findViewById(R.id.lightRawValue);
	}

	protected void onAccesssoryAttached() {
		mSwitchDisplayers = new ArrayList<SwitchDisplayer>();
		for (int i = 0; i < 4; ++i) {
			SwitchDisplayer sd = new SwitchDisplayer(i);
			mSwitchDisplayers.add(sd);
		}
	}

	public void setTemperature(int temperatureFromArduino) {
		/*
		 * Arduino board contains a 6 channel (8 channels on the Mini and Nano,
		 * 16 on the Mega), 10-bit analog to digital converter. This means that
		 * it will map input voltages between 0 and 5 volts into integer values
		 * between 0 and 1023. This yields a resolution between readings of: 5
		 * volts / 1024 units or, .0049 volts (4.9 mV) per unit.
		 * ignore about 4.9. it just set to the 1.
		 */
		
	//smoke sensor
		double voltagemv = temperatureFromArduino * 1; //;
		/*
		 * The change in voltage is scaled to a temperature coefficient of 10.0
		 * mV/degC (typical) for the MCP9700/9700A and 19.5 mV/degC (typical)
         * for the MCP9701/9701A. The out- put voltage at 0 degC is also scaled
         * to 500 mV (typical) and 400 mV (typical) for the MCP9700/9700A and
		 * MCP9701/9701A, respectively. VOUT = TC�TA+V0degC
		 */
		
		double kVoltageAtZeroCmv =0;
		double kTemperatureCoefficientmvperC = 98;
		double ambientTemperatureC = ((double) voltagemv - kVoltageAtZeroCmv)
				/ kTemperatureCoefficientmvperC;
		mTemperature.setText(mTemperatureFormatter.format(ambientTemperatureC));//temperatureF));
		
	}
// dust sensor.
	public void setLightValue(int lightValueFromArduino) {

		double lightvoltagemv = lightValueFromArduino * 1; //

		double lightkVoltageAtZeroCmv =0;
		double lightCoefficientmvperC = 100;
		double ambientLightC = ((double) lightvoltagemv - lightkVoltageAtZeroCmv)
				/ lightCoefficientmvperC;
		//mLightRawView.setText(String.valueOf(lightValueFromArduino));
		mLightView.setText(mLightValueFormatter.format(ambientLightC));
				//.format((100.0 * (double) lightValueFromArduino / 1024.0)));
	}

	public void switchStateChanged(int switchIndex, boolean switchState) {
		if (switchIndex >= 0 && switchIndex < mSwitchDisplayers.size()) {
			SwitchDisplayer sd = mSwitchDisplayers.get(switchIndex);
			sd.onSwitchStateChange(switchState);
		}
	}

	

	public void onTemperature(int temperature) {
		setTemperature(temperature);
	}

	public void onLightChange(int lightValue) {
		setLightValue(lightValue);
	}

	public void onSwitchStateChange(int switchIndex, Boolean switchState) {
		switchStateChanged(switchIndex, switchState);
	}

	

	class SwitchDisplayer {
		private final ImageView mTargetView;
		private final Drawable mOnImage;
		private final Drawable mOffImage;

		SwitchDisplayer(int switchIndex) {
			int viewId, onImageId, offImageId;
			switch (switchIndex) {
			default:
				viewId = R.id.Button1;
				onImageId = R.drawable.indicator_button1_on_noglow;
				offImageId = R.drawable.indicator_button1_off_noglow;
				break;
			case 1:
				viewId = R.id.Button2;
				onImageId = R.drawable.indicator_button2_on_noglow;
				offImageId = R.drawable.indicator_button2_off_noglow;
				break;
			case 2:
				viewId = R.id.Button3;
				onImageId = R.drawable.indicator_button3_on_noglow;
				offImageId = R.drawable.indicator_button3_off_noglow;
				break;
			case 3:
				viewId = R.id.Button4;
				onImageId = R.drawable.indicator_button_capacitive_on_noglow;
				offImageId = R.drawable.indicator_button_capacitive_off_noglow;
				break;
			}
			mTargetView = (ImageView) findViewById(viewId);
			mOffImage = mHostActivity.getResources().getDrawable(offImageId);
			mOnImage = mHostActivity.getResources().getDrawable(onImageId);
		}

		public void onSwitchStateChange(Boolean switchState) {
			Drawable currentImage;
			if (!switchState) {
				currentImage = mOffImage;
			} else {
				currentImage = mOnImage;
			}
			mTargetView.setImageDrawable(currentImage);
		}

	}
}
