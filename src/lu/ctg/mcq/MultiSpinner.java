package lu.ctg.mcq;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Code based on http://stackoverflow.com/questions/5015686/android-spinner-with-multiple-choice
 * Adapted for needs
 * 
 * @author Johann Bernez
 */
public class MultiSpinner extends Spinner implements OnMultiChoiceClickListener, OnCancelListener {

	private List<String> items;
	private boolean[] selected;
	private String defaultText;
	private MultiSpinnerListener listener;

	public MultiSpinner(Context context) {
		super(context);
	}

	public MultiSpinner(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		selected[which] = isChecked;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		setSpinnerAdapter();
		listener.onItemsSelected(selected);
	}

	public boolean performClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selected, this);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setOnCancelListener(this);
		builder.show();
		return true;
	}

	public void setItems(List<String> items, String allText, MultiSpinnerListener listener, boolean[] preselected) {
		this.items = items;
		this.defaultText = allText;
		this.listener = listener;

		this.selected = new boolean[items.size()];
		for (int i = 0; i < selected.length; ++i) {
			this.selected[i] = preselected != null ? preselected[i] : true;
		}
		
		setSpinnerAdapter();
	}
	
	private void setSpinnerAdapter() {
		// refresh text on spinner
		StringBuffer spinnerBuffer = new StringBuffer();
		boolean allSelected = true;
		for (int i = 0; i < items.size(); ++i) {
			if (selected[i]) {
				if (spinnerBuffer.length() > 0) {
					spinnerBuffer.append(", ");
				}
				spinnerBuffer.append(items.get(i));
			} else {
				allSelected = false;
			}
		}
		String spinnerText;
		if (allSelected) {
			spinnerText = defaultText;
		} else {
			spinnerText = spinnerBuffer.toString();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[] { spinnerText });
		setAdapter(adapter);
	}

	public interface MultiSpinnerListener {
		public void onItemsSelected(boolean[] selected);
	}
}
