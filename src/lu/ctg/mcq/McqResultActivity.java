package lu.ctg.mcq;

import lu.ctg.db29.R;
import lu.ctg.mcq.model.McqStage;
import lu.ctg.mcq.model.McqStageBrowser;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Johann Bernez
 */
public class McqResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mcq_result);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public void onBackPressed() {}
	
	public void browseDetails(View view) {
		Intent intent = new Intent(this, McqDetailActivity.class);
		intent.putExtra("stage", new McqStageBrowser(getStage()));
		startActivity(intent);
	}
	
	public void goToHome(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private McqStage getStage() {
		Intent intent = getIntent();
		return (McqStage) intent.getSerializableExtra("stage");
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_mcq_result, container, false);
			
			return rootView;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			
			TextView t = (TextView) getActivity().findViewById(R.id.resultText);
			McqStage stage = ((McqResultActivity) getActivity()).getStage();
			t.setText(getString(R.string.mcq_result_score) + stage.getScore() + "/" + stage.getNumberOfQuestions());
		}
	}
}
