package lu.ctg.mcq.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johann Bernez
 */
public class Question implements Serializable {
	
	private static final long serialVersionUID = -6799624770916100808L;
	
	private String title;
	private String group;
	private String explanation;
	private List<Option> options;
	private List<Answer> answers;
	
	public Question(String title, String explanation, String group) {
		this.title = title;
		this.explanation = explanation;
		this.group = group;
		this.options = new ArrayList<>();
		this.answers = new ArrayList<>();
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}

	public List<Option> getOptions() {
		return this.options;
	}
	
	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
}
