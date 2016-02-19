package time_keeper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class PatternParser {
	protected Matcher m_matcher;
	protected int m_codeIndex;
	protected TrueTime m_lastMinute;
	static final protected String m_timeRegexCaptureGroup 
		= "(\\d+?:\\d+?|\\d+?)";
	static final protected String m_hourMinuteCaptureGroup = "(\\d+):(\\d+)";
	static final Pattern m_hourMinutePattern = 
			Pattern.compile(m_hourMinuteCaptureGroup);
	static final protected String m_minuteCaptureGroup = "(\\d+)";
	static final Pattern m_minutePattern = 
			Pattern.compile(m_minuteCaptureGroup);
	static final protected String m_activityCodeRegexCaptureGroup = 
			"([A-Za-z]+)";
	static final protected String m_totalTimeRegexCaptureGroup = 
			"( => \\d+\\s\\(G\\d+\\))?"; // matches a possible => 45 or such time total
	
	protected boolean isLastMinuteAvailable() {
		return m_lastMinute.isKnown();
	}
	
	public CounterField getCounterField() {
		if (validCodeParsable()) {
			return TimeCodeRepository.counterFieldBelongingToCode(getCode());
		} else {
			return null;
		}
	};
	
	// private function: assumes that pattern matches (doesMatch()==true)
	private String getCode() {
		return m_matcher.group(m_codeIndex);
	}
	
	// private function: assumes that pattern matches (doesMatch()==true)
	private boolean codeIsKnown() {
		return TimeCodeRepository.codeIsKnown(getCode());
	}
	
	protected boolean validCodeParsable() {
		return doesMatch() && codeIsKnown();
	}
		
	protected String timeStatisticsString(int currentTotal, int currentGap) {
		return " => " +
				Integer.toString(currentTotal) + " (G" + 
				Integer.toString(currentGap) + ")";
	}
	
	public int getTimeSpentOfficially() {
		if (validCodeParsable()) {
			return TimeCodeRepository.timeBelongingToCode(getCode());
		} else {
			return 0;
		}
	}
	
	abstract public String produceLineWithCurrentTotal(
			int currentTotal, int currentGap) throws Exception; 
	
	public boolean doesMatch() {
		return m_matcher.matches();
	}
	
	// to be called for begin time.
	protected String makeAbsoluteTime(String oldTime) throws Exception {
		if (m_lastMinute.isKnown()) {
			return makeAbsoluteTime(oldTime, m_lastMinute.toString());
		} else {
			return oldTime;
		}
	}
	
	// to be called for the end time if the activity has a begin time
	protected String makeAbsoluteTime(String endTime, String beginTime) throws Exception {
		TrueTime endTime_TrueTime = parseMinute( endTime );
		TrueTime beginTime_TrueTime = parseMinute( beginTime );
		if (beginTime_TrueTime.getTime() > endTime_TrueTime.getTime() ) {
			// case 6:34 to 55
			int extraHours = beginTime_TrueTime.getHours();
			if (beginTime_TrueTime.getMinutes() > endTime_TrueTime.getMinutes() ) {
				extraHours++;
			}
			endTime_TrueTime.addHours(extraHours);
			return endTime_TrueTime.toString();
		} else {
			return endTime;
		}
	}
	
	abstract protected TrueTime getBeginMinute() throws Exception;
	abstract public TrueTime getEndMinute() throws Exception;
	
	private int calculateTimeDifference(int beginMinute, int endMinute) throws Exception{
		// scenarios: 9:12 9:42 / 
		if (beginMinute <= endMinute) {
			return endMinute - beginMinute;
		} else {
			// end minute is smaller than begin minute
			// scenarios: 8:50 -> 01
			int hourlessBeginMinute = beginMinute % 60;
			if (hourlessBeginMinute > endMinute) {
				// 8:50->01 => 50->01 => 60+1-50=11
				return 60 + endMinute - hourlessBeginMinute;
			} else {
				// 7:10->30
				return endMinute - hourlessBeginMinute;
			}
		}
	}
	
	private int getAbsoluteTimeSpent() throws Exception {
		if (getBeginMinute().isKnown()) {
			return (calculateTimeDifference(
					getBeginMinute().getTime(),getEndMinute().getTime()));
		} else {
			return 0;
		}
	}
	
	protected TrueTime parseMinute(String timeString) {
		Matcher hourMinuteMatcher = m_hourMinutePattern.matcher(timeString);
		if (hourMinuteMatcher.matches()) {
			System.out.println("P12["+hourMinuteMatcher.group(1)+"]");
			return new TrueTime(Integer.parseInt(hourMinuteMatcher.group(1)) * 60 +
				   Integer.parseInt(hourMinuteMatcher.group(2)));
		} else {
			// just a minute given
			Matcher minuteMatcher = m_minutePattern.matcher(timeString);
			if (minuteMatcher.matches()) {
				System.out.println("PM2["+minuteMatcher.group(1)+"]");
				return new TrueTime(Integer.parseInt(minuteMatcher.group(1)));
			} else {
				System.out.println("ERROR!");
				return new TrueTime();
			}
			
		}
	}
	
	public int getProcrastinationTime() throws Exception {
		if (m_lastMinute.isKnown() && getBeginMinute().isKnown()) {
			int timeDifference = calculateTimeDifference(
					m_lastMinute.getTime(),getBeginMinute().getTime());
			if (timeDifference >= 1) { // one minute max time for gap
				timeDifference-=1;
			}
			return timeDifference;
		} else {
			return 0; // at the start of the day, procrastination doesn't count
		}
		
	}
	
	// extra time spent since it was too hard to stop
	public int getStoppingTime() throws Exception{
		// allow 1 minute for administrative rounding errors
		if (getAbsoluteTimeSpent() > (getTimeSpentOfficially() + 1) ) {
			return (getAbsoluteTimeSpent() - getTimeSpentOfficially() - 1);
		} else {
			return 0;
		}
	}
	
	public PatternParser(String patternAsString, int codeIndex, String line,
			TrueTime lastMinute) {
		Pattern pattern = Pattern.compile(patternAsString);
		m_matcher = pattern.matcher( line );
		m_codeIndex = codeIndex;
		m_lastMinute = lastMinute;
	}
}