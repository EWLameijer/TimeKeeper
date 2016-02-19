package time_keeper;

class RegularActivityParser extends PatternParser {
	private final static String m_patternAsString =
			m_timeRegexCaptureGroup + m_activityCodeRegexCaptureGroup + 
			m_timeRegexCaptureGroup + m_totalTimeRegexCaptureGroup;
	private final static int m_indexOfCode = 2;
	
	public String produceLineWithCurrentTotal(int currentTotal, 
			int currentGap) throws Exception  {
		String absoluteBeginTime = makeAbsoluteTime(m_matcher.group(1));
		String absoluteEndTime = makeAbsoluteTime(m_matcher.group(3), 
				absoluteBeginTime);
		return  absoluteBeginTime + m_matcher.group(2) +
				absoluteEndTime +
				timeStatisticsString( currentTotal, currentGap );
	}
	
	protected TrueTime getBeginMinute() throws Exception {
		return parseMinute(makeAbsoluteTime(m_matcher.group(1))); 
	}
	
	public TrueTime getEndMinute() throws Exception {
		String absoluteBeginTime = makeAbsoluteTime(m_matcher.group(1));
		String absoluteEndTime = makeAbsoluteTime(m_matcher.group(3), 
				absoluteBeginTime);
		return parseMinute(absoluteEndTime); 
	}
	
	public RegularActivityParser(String line, TrueTime lastMinute) {
		super(m_patternAsString,m_indexOfCode,line,lastMinute);
	}
}