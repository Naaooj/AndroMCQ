package lu.ctg.mcq;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import lu.ctg.mcq.MultiSpinner.MultiSpinnerListener;
import lu.ctg.mcq.model.McqStage;
import lu.ctg.mcq.model.Question;
import lu.ctg.mcq.model.XmlParser;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Johann Bernez
 */
public class MainActivity extends Activity implements MultiSpinnerListener {
	
	//
	private static boolean loaded = false;
	
	// 
	private static List<Question> questions;
	private static int[] questionsRaws = new int[] {
		R.raw.questions_ch2,
		R.raw.questions_ch3,
		R.raw.questions_ch4,
		R.raw.questions_ch5,
		R.raw.questions_ch6,
		R.raw.questions_ch7
	};
	private static boolean[] selected = null;
	private static int nbrOfQuestions = 0;
	private boolean quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		if (!loaded) {
			try {
				InputStream[] streams = new InputStream[questionsRaws.length];
				selected = new boolean[questionsRaws.length];
				for (int i = 0; i < questionsRaws.length; ++i) {
					streams[i] = getResources().openRawResource(questionsRaws[i]);
					selected[i] = true;
				}
				questions = new XmlParser().parse(streams);
				Log.v("MainActivity", "" + questions.size());
			} catch (Exception e) {
				Log.e("MainActivity", "Exception while parsing", e);
			}
			loaded = true;
		}
	}

	@Override
	public void onItemsSelected(boolean[] sel) {
		selected = sel;
	}

	@Override
	public void onBackPressed() {
		if (quit) {
			finish();
		}
		if (!quit) {
			Toast t = Toast.makeText(this, getString(R.string.press_to_quit), Toast.LENGTH_SHORT);
			t.show();
			quit = true;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			
			return rootView;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			
			getActivity().getActionBar().setTitle(getString(R.string.title_activity_main));
			
			MultiSpinner spinner = (MultiSpinner) getActivity().findViewById(R.id.groupSpinner);
			if (spinner != null) {
				spinner.setItems(Arrays.asList(getResources().getStringArray(R.array.mcq_groups)), getString(R.string.mcq_groups_all), (MultiSpinnerListener) getActivity(), selected);
			}

			if (nbrOfQuestions != 0 && nbrOfQuestions != questions.size()) {
				EditText text = (EditText) getActivity().findViewById(R.id.editText1);
				text.setText(Integer.toString(nbrOfQuestions));
			}
		}
	}
	
	public void startMCQ(View view) {
		try {
			EditText text = (EditText) findViewById(R.id.editText1);
			nbrOfQuestions = Integer.parseInt(text.getText().toString());
		} catch (Exception e) {
			Toast t = Toast.makeText(this, getString(R.string.main_all_loaded), Toast.LENGTH_SHORT);
			t.show();
			nbrOfQuestions = questions.size();
		}	
			
		try {
			List<Question> questions = getQuestionsRandomly(nbrOfQuestions);
			
			CheckBox shuffle = (CheckBox) findViewById(R.id.suffleCb);
			if (shuffle.isChecked()) {
				Collections.shuffle(questions);
			}
			
			McqStage stage = new McqStage(questions);
			
	    	Intent intent = new Intent(this, McqActivity.class);
	    	intent.putExtra("stage", stage);
	    	startActivity(intent);
		} catch (Exception e) {
			Toast t = Toast.makeText(this, "Oups, une exception est survenue...", Toast.LENGTH_LONG);
			Log.e("MainActivity", "Unable to initialize list", e);
			t.show();
		}
    }
	
	private List<Question> getQuestionsRandomly(int maxNbrOfQuestions) {
		List<Question> refinedQuestions = refineQuestions();
		
		int max = (maxNbrOfQuestions >= refinedQuestions.size() || maxNbrOfQuestions <= 0) ? refinedQuestions.size() : maxNbrOfQuestions;
		
		Random rng = new Random();
		Set<Integer> generated = new HashSet<>();
		while (generated.size() < max) {
		    Integer next = rng.nextInt(refinedQuestions.size()) + 1;
		    generated.add(next);
		}
		
		List<Question> selected = new ArrayList<>(max);
		for (Integer i : generated) {
			selected.add(refinedQuestions.get(i-1));
		}
		
		return selected;
	}
	
	private List<Question> refineQuestions() {
		List<Question> refinedQuestions = new ArrayList<>();
		Map<String, List<Question>> groupToQuestions = new TreeMap<>();
		for (Question q : questions) {
			List<Question> l = groupToQuestions.get(q.getGroup());
			if (l == null) {
				l = new ArrayList<>();
				groupToQuestions.put(q.getGroup(), l);
			}
			l.add(q);
		}
		int index = 0;
		for (List<Question> l : groupToQuestions.values()) {
			if (selected[index]) {
				refinedQuestions.addAll(l);
			}
			index++;
		}
		return refinedQuestions;
	}
}
