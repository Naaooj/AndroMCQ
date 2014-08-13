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
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Johann Bernez
 */
public class McqActivity extends Activity implements OnNavigateListener {

	private Chronometer chronometer;
	private long baseTimer;
	private long timer;
	
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
	public void goToPrev() {}
	
	@Override
	public void goToNext() {
		startActivity(McqActivity.class);
	}
	
	@Override
	public void goToEnd() {
		startActivity(McqResultActivity.class);
	}
	
	private void startActivity(Class<?> activityClass) {
		Intent intent = new Intent(this, activityClass);
		intent.putExtra("stage", getStage());
		intent.putExtra("timer", timer + 1000l);
		intent.putExtra("baseTimer", baseTimer);
		startActivity(intent);
	}
	
	private McqStage getStage() {
		return (McqStage) getIntent().getSerializableExtra("stage");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mcq, menu);
		
		timer = getIntent().getLongExtra("timer", 0l);
		
		MenuItem item = (MenuItem) menu.findItem(R.id.action_settings);
		LinearLayout ll = (LinearLayout) item.getActionView();
		
		chronometer = (Chronometer) ll.findViewById(R.id.chrono);
		baseTimer = SystemClock.elapsedRealtime() - timer;
		chronometer.setBase(baseTimer);
		chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				timer = SystemClock.elapsedRealtime() - chronometer.getBase();
			}
		});
		chronometer.start();
		
		return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	private static class PlaceholderFragment extends Fragment {
		
		private Button next;
		private Button finish;
		private List<CheckBox> cbs;
		private OnNavigateListener navigationListener;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_mcq, container, false);
			
			McqStage stage = ((McqActivity) getActivity()).getStage();
			
			if (stage != null && !stage.isCompleted()) {
				updateActivityTitle(stage);
				
				LinearLayout content = (LinearLayout) rootView;
				
				Question q = stage.getNextQuestion();
				
				TextView title = new TextView(getActivity());
				title.setTextAppearance(getActivity(), R.style.mcq_text_style);
				title.setText(q.getTitle());
				
				content.addView(title);
				cbs = new ArrayList<>(q.getOptions().size());
				int i = 1;
				for (Option o : q.getOptions()) {
					CheckBox c = new CheckBox(getActivity());
					c.setId(o.getValue());
					c.setText(o.getText());
					c.setOnClickListener(stageClickListener(c, stage));
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
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			
			try {
				navigationListener = (OnNavigateListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString() + " must implement OnNavigateListener");
			}
		}
		
		private void updateActivityTitle(McqStage stage) {
			String t = (String) getActivity().getString(R.string.title_activity_mcq);
			t += " " + (stage.getProgression()+1) + "/" + stage.getNumberOfQuestions();
			getActivity().getActionBar().setTitle(t);
		}
		
		private void createButtons(LinearLayout content, McqStage stage) {
			LinearLayout childContainer = new LinearLayout(getActivity());
			childContainer.setGravity(Gravity.RIGHT);
			childContainer.setOrientation(LinearLayout.HORIZONTAL);
			content.addView(childContainer);
			
			if (stage.getProgression() < stage.getNumberOfQuestions() - 1) {
				next = new Button(getActivity());
				next.setEnabled(false);
				next.setTextAppearance(getActivity(), R.style.mcq_button_style);
				next.setBackgroundResource(R.drawable.mcq_button);
				next.setText(getActivity().getString(R.string.mcq_button_next));
				next.setOnClickListener(stageClickListener(next, stage));
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(5, 0, 5, 0);
				params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
				next.setLayoutParams(params);
				
				childContainer.addView(next);
			}
			
			finish = new Button(getActivity());
			finish.setTextAppearance(getActivity(), R.style.mcq_button_style);
			finish.setBackgroundResource(R.drawable.mcq_button);
			finish.setText(getActivity().getString(R.string.mcq_button_finish));
			finish.setOnClickListener(stageClickListener(finish, stage));
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
			finish.setLayoutParams(params);
			
			childContainer.addView(finish);
		}
		
		View.OnClickListener stageClickListener(final Button button, final McqStage stage) {
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
						if (stage != null) {
							int progression = stage.getProgression();
							for (CheckBox c : cbs) {
								if (c.isChecked()) {
									Answer a = new Answer(c.getId());
									stage.addAnswers(progression, a);
								}
							}
						}
					}
					if (button.equals(next)) {
						navigationListener.goToNext();
					}
					if (button.equals(finish)) {
						navigationListener.goToEnd();
					}
				}
			};
		}
	}
}
