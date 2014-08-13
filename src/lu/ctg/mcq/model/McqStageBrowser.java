package lu.ctg.mcq.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Johann Bernez
 */
public class McqStageBrowser implements Serializable {

	private static final long serialVersionUID = 5773052942189106428L;
	
	private int navigationIndex = 0;
	private final McqStage stage;
	
	public McqStageBrowser(McqStage stage) {
		this.stage = stage;
	}
	
	public McqStage getStage() {
		return this.stage;
	}
	
	public Question getQuestion() {
		return getQuestionAtNavigationIndex();
	}
	
	/**
	 * @return la liste des reponses de l'utilisateur
	 */
	public List<Answer> getAnswers() {
		List<Answer> answers = this.stage.getAnswers(navigationIndex);
		if (answers == null) {
			answers = Collections.emptyList();
		}
		return answers;
	}
	
	public boolean isPrev() {
		return navigationIndex > 0;
	}
	
	public boolean isNext() {
		return navigationIndex < stage.getNumberOfQuestions() - 1;
	}
	
	public void navigateNext() {
		navigationIndex++;
	}
	
	public void navigatePrev() {
		navigationIndex--;
	}
	
	public int getNavigationIndex() {
		return navigationIndex;
	}
	
	private Question getQuestionAtNavigationIndex() {
		if (navigationIndex < 0 || navigationIndex >= this.stage.getNumberOfQuestions()) {
			return null;
		}
		return this.stage.questions.get(navigationIndex);
	}
	
	public List<AnswerResult> getAnswersResults() {
		Question q = getQuestion();
		List<Answer> answers = getAnswers();
		
		Map<Option, Answer> oToA = new HashMap<>();
		for (Option o : q.getOptions()) {
			Answer answer = null;
			for (Answer a : answers) {
				if (o.getValue() == a.getValue()) {
					answer = a;
					break;
				}
			}
			oToA.put(o, answer);
		}
		
		List<AnswerResult> results = new ArrayList<>();
		for (Entry<Option, Answer> e : oToA.entrySet()) {
			Answer answer = null;
			for (Answer a : q.getAnswers()) {
				if (a.equals(e.getValue())) {
					answer = a;
					break;
				} else if (a.getValue() == e.getKey().getValue()) {
					results.add(new AnswerResult(a, e.getKey(), false));
				}
			}
			if (answer != null) {
				results.add(new AnswerResult(answer, e.getKey(), true));
			}
		}
		
		Collections.sort(results, new Comparator<AnswerResult>() {
			@Override public int compare(AnswerResult lhs, AnswerResult rhs) {
				if (lhs.getValue() == rhs.getValue()) {
					return 0;
				} else if (lhs.getValue() < rhs.getValue()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		return results;
	}
	
	public static class AnswerResult extends Answer {

		private static final long serialVersionUID = 9025377133699943845L;
		
		private final Answer answer;
		private final Option option;
		private final boolean success;
		
		public AnswerResult(Answer answer, Option option, boolean success) {
			super(answer.getValue());
			this.answer = answer;
			this.option = option;
			this.success = success;
		}
		
		@Override public int getValue() {
			return answer.getValue();
		}
		
		public Option getOption() {
			return option;
		}
		
		public boolean isSuccess() {
			return this.success;
		}
	}
}
