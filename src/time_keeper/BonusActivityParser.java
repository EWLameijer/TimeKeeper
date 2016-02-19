package time_keeper;

class BonusActivityParser extends PatternParser {
	private final static String m_patternAsString = 
			m_activityCodeRegexCaptureGroup + "\\+" + m_timeRegexCaptureGroup +
			"\\|" +m_timeRegexCaptureGroup + m_totalTimeRegexCaptureGroup;
	private final static int m_indexOfCode = 1;
	
	protected TrueTime getBeginMinute() throws Exception {
		if (isLastMinuteAvailable()) {
			return new TrueTime(m_lastMinute.getTime()+1);
		} else {
			return new TrueTime(); // don't claim any gap info
		}
	}
	
	public TrueTime getEndMinute() {
		return parseMinute(m_matcher.group(3)); 
	}
	
	public int getTimeSpentOfficially() {
		if (validCodeParsable()) {
			return Integer.parseInt(m_matcher.group(2));
		} else {
			return 0;
		}
	}

	
	public String produceLineWithCurrentTotal(int currentTotal, int currentGap) throws Exception {
		return m_matcher.group(1) + "+" + m_matcher.group(2) + "|" + 
				makeAbsoluteTime(m_matcher.group(3)) + 
				timeStatisticsString( currentTotal, currentGap);
	}
	
	public BonusActivityParser(String line, TrueTime lastMinute) {
		super(m_patternAsString,m_indexOfCode,line,lastMinute);
	}
}