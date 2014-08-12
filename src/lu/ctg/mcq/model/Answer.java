package lu.ctg.mcq.model;

import java.io.Serializable;

/**
 * @author Johann Bernez
 */
public class Answer implements Serializable {

	private static final long serialVersionUID = 5541656273112037063L;
	
	private final int value;
	
	public Answer(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Answer)) {
			return false;
		}
		return ((Answer) o).getValue() == getValue();
	}
	
	@Override
	public int hashCode() {
		int hash = 31 * (value + 1);
		return hash;
	}
}
