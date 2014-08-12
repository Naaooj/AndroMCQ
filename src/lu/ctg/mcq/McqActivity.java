package lu.ctg.mcq;

import java.util.ArrayList;
import java.util.List;

import lu.ctg.mcq.model.Answer;
import lu.ctg.mcq.model.McqStage;
import lu.ctg.mcq.model.Option;
import lu.ctg.mcq.model.Question;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Johann Bernez
 */
public class McqActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mcq);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public void onBackPressed() {}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		private Button next;
		private Button finish;
		private List<CheckBox> cbs;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_mcq, container, false);
			
			McqStage stage = getStage();
			
			if (stage != null && !stage.isCompleted()) {
				updateActivityTitle(stage);
				
				LinearLayout content = (LinearLayout) rootView;
				
				Question q = stage.getNextQuestion();
				
				TextView title = new TextView(getActivity());
				title.setText(q.getTitle());
				
				content.addView(title);
				cbs = new ArrayList<>(q.getOptions().size());
				int i = 1;
				for (Option o : q.getOptions()) {
					CheckBox c = new CheckBox(getActivity());
					c.setId(o.getValue());
					c.setText(o.getText());
					c.setOnClickListener(stageClickListener(c));
					if (i%2==0) {
						c.setBackgroundColor(getActivity().getResources().getColor(R.color.cb_background_color));
					}
					i++;
					cbs.add(c);
					content.addView(c);
				}
				
				createButtons(content, stage);
			}
			
			return rootView;
		}
		
		private McqStage getStage() {
			Intent intent = getActivity().getIntent();
			return (McqStage) intent.getSerializableExtra("stage");
		}
		
		private void updateActivityTitle(McqStage stage) {
			String t = (String) getActivity().getString(R.string.title_activity_mcq);
			t += " " + (stage.getProgression()+1) + "/" + stage.getNumberOfQuestions();
			getActivity().getActionBar().setTitle(t);
		}
		
		private void createButtons(LinearLayout content, McqStage stage) {
			LinearLayout childContainer = content;
			if (stage.getProgression() < stage.getNumberOfQuestions() - 1) {
				childContainer = new LinearLayout(getActivity());
				childContainer.setGravity(Gravity.RIGHT);
				childContainer.setOrientation(LinearLayout.HORIZONTAL);
				content.addView(childContainer);
				
				next = new Button(getActivity());
				next.setEnabled(false);
				next.setTextAppearance(getActivity(), R.style.mcq_button_style);
				next.setBackgroundResource(R.drawable.mcq_button);
				next.setText(getActivity().getString(R.string.mcq_button_next));
				next.setOnClickListener(stageClickListener(next));
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(5, 0, 5, 0);
				params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
				next.setLayoutParams(params);
				childContainer.addView(next);
			}
			
			finish = new Button(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
			finish.setLayoutParams(params);
			finish.setTextAppearance(getActivity(), R.style.mcq_button_style);
			finish.setBackgroundResource(R.drawable.mcq_button);
			finish.setText(getActivity().getString(R.string.mcq_button_finish));
			finish.setOnClickListener(stageClickListener(finish));
			childContainer.addView(finish);
		}
		
		View.OnClickListener stageClickListener(final Button button) {
			return new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (next != null && button instanceof CheckBox) {
						boolean enabled = false;
						for (CheckBox c : cbs) {
							enabled |= c.isChecked();
						}
						next.setEnabled(enabled);
					}
					if (button.equals(next) || button.equals(finish)) {
						McqStage stage = getStage();
						if (stage != null) {
							for (CheckBox c : cbs) {
								if (c.isChecked()) {
									Answer a = new Answer(c.getId());
									stage.addAnswers(stage.getProgression(), a);
								}
							}
						}
					}
					if (button.equals(next)) {
						Intent intent = new Intent(getActivity(), McqActivity.class);
				    	intent.putExtra("stage", getStage());
				    	startActivity(intent);
					}
					if (button.equals(finish)) {
						Intent intent = new Intent(getActivity(), McqResultActivity.class);
						intent.putExtra("stage", getStage());
						startActivity(intent);
					}
				}
			};
		}
	}
}
