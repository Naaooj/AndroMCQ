package lu.ctg.mcq;

import java.util.List;

import lu.ctg.mcq.model.McqStageBrowser;
import lu.ctg.mcq.model.McqStageBrowser.AnswerResult;
import lu.ctg.mcq.model.Option;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author Johann Bernez
 */
public class McqDetailActivity extends Activity implements OnNavigateListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mcq_detail);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mcq_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionPrevDetail:
			goToPrev();
			return true;
		case R.id.actionNextDetail:
			goToNext();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void goToPrev() {
		McqStageBrowser stage = getFragment().getStage();
		if (stage.isPrev()) {
			stage.navigatePrev();
			navigate(stage);
		}
	}

	@Override
	public void goToNext() {
		McqStageBrowser stage = getFragment().getStage();
		if (stage.isNext()) {
			stage.navigateNext();
			navigate(stage);
		}
	}
	
	private void navigate(McqStageBrowser stage) {
		Intent intent = new Intent(this, McqDetailActivity.class);
		intent.putExtra("stage", stage);
		startActivity(intent);
		finish();
	}

	private PlaceholderFragment getFragment() {
		return (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private ExpandableListView optionsView;
		private Button prev;
		private Button next;
		private Button finish;
		
		private OnNavigateListener navigationListener;
		
		public PlaceholderFragment() {
		}

		private McqStageBrowser getStage() {
			Intent intent = getActivity().getIntent();
			return (McqStageBrowser) intent.getSerializableExtra("stage");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_mcq_detail, container, false);
			LinearLayout mainContent = (LinearLayout) rootView;
			LayoutParams params;
			
			final McqStageBrowser stage = getStage();
			
			optionsView = new ExpandableListView(getActivity());
			optionsView.setBackgroundColor(getResources().getColor(R.color.exp_list_bg_color));
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			optionsView.setLayoutParams(params);
			optionsView.setAdapter(new BaseExpandableListAdapter() {
				@Override
				public Object getChild(int groupPosition, int childPosition) {
					return stage.getQuestion().getOptions().get(childPosition);
				}

				@Override
				public long getChildId(int groupPosition, int childPosition) {
					return childPosition;
				}

				@Override
				@SuppressLint("InflateParams")
				public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
					if (convertView == null) {
						LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView = inflater.inflate(R.layout.mcq_option_item, null);
					}
					
					TextView view = (TextView) convertView.findViewById(R.id.mcqOptionItem);
					Option o = (Option) getChild(groupPosition, childPosition);
					if (groupPosition == 0) {
						view.setText(stage.getQuestion().getTitle());
					} else {
						view.setText(o.getTextualValue() + " - " + o.getText());
					}
					return view;
				}

				@Override
				public int getChildrenCount(int groupPosition) {
					return groupPosition == 0 ? 1 : stage.getQuestion().getOptions().size();
				}

				@Override
				public Object getGroup(int groupPosition) {
					if (groupPosition == 0) {
						return getActivity().getString(R.string.mcq_detail_group_question);
					} else {
						return getActivity().getString(R.string.mcq_detail_group_answer);
					}
				}

				@Override
				public int getGroupCount() {
					return 2;
				}

				@Override
				public long getGroupId(int groupPosition) {
					return groupPosition;
				}

				@Override
				@SuppressLint("InflateParams")
				public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
					if (convertView == null) {
						LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						convertView = inflater.inflate(R.layout.mcq_option_group, null);
					}
					
					TextView view = (TextView) convertView.findViewById(R.id.mcqOptionGroup);
					view.setText((String) getGroup(groupPosition));
					return view;
				}

				@Override
				public boolean hasStableIds() {
					return false;
				}

				@Override
				public boolean isChildSelectable(int groupPosition, int childPosition) {
					return false;
				}
			});
			optionsView.expandGroup(0);
			mainContent.addView(optionsView);
			
			ScrollView scrollContent = new ScrollView(getActivity());
			scrollContent.setFillViewport(true);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			scrollContent.setLayoutParams(params);
			mainContent.addView(scrollContent);
			
			final LinearLayout content = new LinearLayout(getActivity());
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			content.setLayoutParams(params);
			content.setOrientation(LinearLayout.VERTICAL);
			scrollContent.addView(content);
			
			LinearLayout answers = new LinearLayout(getActivity());
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			answers.setOrientation(LinearLayout.HORIZONTAL);
			answers.setLayoutParams(params);
			content.addView(answers);
			
			List<AnswerResult> results = stage.getAnswersResults();
			for (int i = 0; i < results.size(); ++i) {
				TextView v = new TextView(getActivity());
				if (results.get(i).isSuccess()) {
					v.setTextColor(getResources().getColor(R.color.answer_right));
				} else {
					v.setTextColor(getResources().getColor(R.color.answer_wrong));
				}
				v.setText((i > 0 ? ", " : "") + results.get(i).getOption().getTextualValue());
				answers.addView(v);
			}
			
			// Using WebView instead of TextView because it lacks rendering functionality
			WebView explanations = new WebView(getActivity());
			String data = "<p style=\"text-align: justify;\">" + stage.getQuestion().getExplanation() + "</p>";
			explanations.loadData(data, "text/html", null);
			// Adding transparency
			explanations.setBackgroundColor(0x00000000);
			explanations.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			content.addView(explanations);
			
			createButtons(content, stage);
			
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
		
		private void createButtons(ViewGroup content, McqStageBrowser stage) {
			LinearLayout childContainer = new LinearLayout(getActivity());
			LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			childContainer.setLayoutParams(params);
			childContainer.setWeightSum(3);
			childContainer.setOrientation(LinearLayout.HORIZONTAL);
			content.addView(childContainer);
			
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 0, 5, 0);
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
			params.weight = 1;
			
			prev = new Button(getActivity());
			prev.setEnabled(stage.isPrev());
			prev.setTextAppearance(getActivity(), R.style.mcq_button_style);
			prev.setBackgroundResource(R.drawable.mcq_button);
			prev.setText(getActivity().getString(R.string.mcq_button_prev));
			prev.setOnClickListener(stageClickListener(prev));
			prev.setLayoutParams(params);
			childContainer.addView(prev);
			
			next = new Button(getActivity());
			next.setEnabled(stage.isNext());
			next.setTextAppearance(getActivity(), R.style.mcq_button_style);
			next.setBackgroundResource(R.drawable.mcq_button);
			next.setText(getActivity().getString(R.string.mcq_button_next));
			next.setOnClickListener(stageClickListener(next));
			next.setLayoutParams(params);
			childContainer.addView(next);
			
			finish = new Button(getActivity());
			finish.setTextAppearance(getActivity(), R.style.mcq_button_style);
			finish.setBackgroundResource(R.drawable.mcq_button);
			finish.setText(getActivity().getString(R.string.mcq_button_finish));
			finish.setOnClickListener(stageClickListener(finish));
			finish.setLayoutParams(params);
			childContainer.addView(finish);
		}
		
		View.OnClickListener stageClickListener(final Button button) {
			return new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (button.equals(prev)) {
						navigationListener.goToPrev();
					}
					if (button.equals(next)) {
						navigationListener.goToNext();
					}
					if (button.equals(finish)) {
						Intent intent = new Intent(getActivity(), McqResultActivity.class);
						intent.putExtra("stage", getStage().getStage());
						startActivity(intent);
					}
				}
			};
		}
	}
}
