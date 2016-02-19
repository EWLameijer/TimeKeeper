package time_keeper;

import java.util.Hashtable;
import java.util.Map;

// TimeCodeRepository could be a singleton or a static class.
// Singleton: advantage is that initialization is guaranteed; disadvantage
// is that call is more verbose.
// Static class: disadvantage is that one needs to explicitly take care of
// initialization in the main class
// regular: could actually be reasonable, but wastes too much time in 
// construction and destruction. Besides, for now there is only a single
// repository - having two would be fundamentally incorrect.
class TimeCodeRepository {
	static private Map<String,Integer> m_timeTable;
	static private Map<String,CounterField> m_categoryTable;

	static private CounterField m_gapField;
	static private CounterField m_debtField;
	static private CounterField m_workField;
	static private CounterField m_maintenanceField;
	static private CounterField m_wowField;
	
	private static void addTranslation(String code, int timeSpent, 
			CounterField counterField) {
		String standardizedCode = code.toLowerCase();
		if (codeIsKnown(code)) {
			System.out.println("Error: trying to add duplicate activity code!");
		} else {
			m_timeTable.put(standardizedCode, timeSpent);
			m_categoryTable.put(standardizedCode, counterField);
		}
	}
	
	public static boolean codeIsKnown(String code) {
		return m_timeTable.containsKey(code.toLowerCase());
	}
	
	public static int timeBelongingToCode(String code) {
		return m_timeTable.get(code.toLowerCase());
	}
	
	public static CounterField counterFieldBelongingToCode(String code) {
		return m_categoryTable.get(code.toLowerCase());
	}
		
	private static void addSynonym(String synonym, String normalName) {
		String standardizedNormalCode = normalName.toLowerCase();
		String standardizedSynonym = synonym.toLowerCase();
		int timeSpentByOriginal = m_timeTable.get(standardizedNormalCode);
		CounterField fieldForOriginal = m_categoryTable.get(standardizedNormalCode);
		addTranslation(standardizedSynonym,timeSpentByOriginal,fieldForOriginal);
	}
	
	public static void init(CounterField gapField, CounterField debtField, 
			CounterField workField, CounterField maintenanceField, 
			CounterField wowField) {
		m_timeTable = new Hashtable<>();
		m_categoryTable = new Hashtable<>();
		
		m_gapField = gapField;
		m_debtField = debtField;
		m_workField = workField;
		m_maintenanceField = maintenanceField;
		m_wowField = wowField;

		addTranslation("aankl",5,m_maintenanceField);
		addTranslation("Adep",15,m_debtField);
		addTranslation("Afwas",30,m_maintenanceField); // afwas
		addSynonym("afw","Afwas");
		addTranslation("AH",20,m_maintenanceField);
		addTranslation("Chin",5,m_wowField);
		addSynonym("Ch","Chin");
		addTranslation("Cleo",30,m_wowField);
		addTranslation("dou",20,m_maintenanceField);
		addTranslation("f",5,m_maintenanceField);
		addTranslation("g",0,m_gapField); // gap
		addTranslation("HR",20,m_debtField);
		addTranslation("J",30,m_wowField); //java
		addTranslation("KE",60,m_maintenanceField);
		addTranslation("kof",1,m_maintenanceField);
		addSynonym("k","kof");
		addTranslation("lol",10,m_debtField);
		addTranslation("ME",20,m_maintenanceField);
		addTranslation("MO",15,m_maintenanceField);
		addTranslation("ond",0,m_maintenanceField);
		addTranslation("ontb",20,m_maintenanceField);
		addSynonym("ont","ontb");
		addTranslation("OO",15,m_maintenanceField);
		addTranslation("opap",5,m_maintenanceField); // oud papier
		addTranslation("Opr",5,m_debtField);
		addTranslation("p",2,m_maintenanceField);
		addTranslation("vuil",5,m_maintenanceField);
		addTranslation("sch",5,m_maintenanceField);
		addTranslation("sport",90,m_maintenanceField);
		addTranslation("strijk",10,m_maintenanceField);
		addTranslation("was",5,m_maintenanceField);
		addTranslation("WC",5,m_maintenanceField);
		addTranslation("werk",530,m_workField);
		addSynonym("w","werk");
		addTranslation("wow",0,m_wowField);
		addTranslation("WT",30,m_wowField);
		addTranslation("ZB",15,m_maintenanceField);	
	}
}