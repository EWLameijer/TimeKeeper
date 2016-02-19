package time_keeper;

// Gives time with info on whether it is known
// time is set/received in minutes
class TrueTime {
	private boolean m_isKnown;
	private int m_hours;
	private int m_minutes;
	
	public boolean isKnown() {
		return m_isKnown;
	}
	
	public int getTime() throws Exception {
		if (m_isKnown) {
			return m_hours*60 + m_minutes;
		} else {
			throw new Exception("Undefined time cannot be requested!");
		}
	}
	
	public int getHours() throws Exception {
		if (m_isKnown) {
			return m_hours;
		} else {
			throw new Exception("Undefined time cannot be requested!");
		}
	}
	
	public int getMinutes() throws Exception {
		if (m_isKnown) {
			return m_minutes;
		} else {
			throw new Exception("Undefined time cannot be requested!");
		}
	}
	
	public void setTime(int time) {
		m_isKnown = true;
		m_hours = time / 60;
		m_minutes = time % 60;
	}
	
	public void addHours(int hours) {
		m_hours += hours;
	}
	
	public String toString() {
		String timeAsText = m_hours + ":";
		if (m_minutes < 10) {
			timeAsText += "0";
		}
		timeAsText += m_minutes; 
		return timeAsText;
	}
	
	public TrueTime() {
		m_isKnown = false;
		m_hours = 0;
		m_minutes = 0;
	}
	
	public TrueTime(int time) {
		setTime(time);
	}	
}