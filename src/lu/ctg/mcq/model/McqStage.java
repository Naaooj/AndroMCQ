package lu.ctg.mcq.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Johann Bernez
 */
public class McqStage implements Serializable {

	private static final long serialVersionUID = 1891967305882220374L;
	
	private int score = -1;
	private long time = 0l;
	final List<Question> questions;
	private final Map<Integer, List<Answer>> answers;
	
	public McqStage(List<Question> questions) {
		this.questions = questions;
		this.answers = new HashMap<>(questions.size());
	}
	
	public int getProgression() {
		return this.answers.size();
	}
	
	public boolean isCompleted() {
		return this.questions.size() == this.answers.size();
	}
	
	public int getNumberOfQuestions() {
		return this.questions.size();
	}
	
	public Question getNextQuestion() {
		return this.questions.get(getProgression());
	}
	
	public List<Answer> getAnswers(int index) {
		return this.answers.get(index);
	}
	
	public long getTime() {
		return this.time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void addAnswers(int index, Answer...answers) {
		if (answers == null) {
			return;
		}
		List<Answer> a = null;
		if ((a=this.answers.get(index)) == null) {
			a = new ArrayList<>(answers.length);
			this.answers.put(index, a);
		}
		a.addAll(Arrays.asList(answers));
	}
	
	public int getScore() {
		if (score == -1) {
			computeScore();
		}
		return score;
	}
	
	private void computeScore() {
		Question q;
		score = 0;
		for (Entry<Integer, List<Answer>> e : answers.entrySet()) {
			q = questions.get(e.getKey());
			if (areAnswersCorrect(q.getAnswers(), e.getValue())) {
				score++;
			}
		}
	}
	
	private boolean areAnswersCorrect(List<Answer> answers, List<Answer> responses) {
		int nbrOfAnswerMatching = 0;
		for (Answer a : answers) {
			if (responses.contains(a)) {
				nbrOfAnswerMatching++;
			}
		}
		return answers.size() == nbrOfAnswerMatching;
	}
}
