package time_keeper;

import javax.swing.JTextField;

class CounterField extends JTextField {
	private static final long serialVersionUID = 1L;
	int m_counter;
	
	private void showText() {
		setText(Integer.toString(m_counter));
	}
	
	public void reset() {
		m_counter = 0;
		showText();
	}
	
	public void add(int number) {
		m_counter += number;
		showText();
	}
	
	public int getValue() {
		return m_counter;
	}
	
	CounterField() {
		super();
		reset();
	}
}