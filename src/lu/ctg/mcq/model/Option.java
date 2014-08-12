package lu.ctg.mcq.model;

import java.io.Serializable;

/**
 * @author Johann Bernez
 */
public class Option implements Serializable {

	private static final long serialVersionUID = 4316177705370873570L;
	
	private final int value;
	private final String textualValue;
	private final String text;
	
	public Option(int value, String textualValue, String text) {
		this.value = value;
		this.textualValue = textualValue;
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public String getTextualValue() {
		return textualValue;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Option)) {
			return false;
		}
		Option op = (Option) o;
		boolean eq = isEqual(getText(), op.getText());
		eq &= isEqual(getTextualValue(), op.getTextualValue());
		eq &= getValue() == op.getValue();
		return eq;
	}
	
	@Override
	public int hashCode() {
		int hash =  (getText() != null ? getText().hashCode() : 1);
		hash = 31 * hash + (getTextualValue() != null ? getTextualValue().hashCode() : 1);
		hash = 31 * hash + getValue();
		return hash;
	}
	
	private boolean isEqual(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		} else if (s1 != null && s2 != null && s1.trim().equals(s2.trim())) {
			return true;
		}
		return false;
	}
}
