package time_keeper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TimeKeeper extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;

	private static final String saveFileName = "Timekeeper_log.txt";
	private static final String configurationFileName = "categories.txt";
	private List<String> m_parsingErrors;
	private List<String> m_events;

	// the names of the time categories (the first/zeroth element will always be
	// used to indicate time 'holes')
	private List<String> m_categoryNames;

	private JTextArea m_writingPad;
	private JLabel m_gapLabel;
	private CounterField m_gapField;
	private JLabel m_debtLabel;
	private CounterField m_debtField;
	private JLabel m_workLabel;
	private CounterField m_workField;
	private JLabel m_maintenanceLabel;
	private CounterField m_maintenanceField;

	private JLabel m_wowLabel;
	private CounterField m_wowField;
	private JButton m_copyToClipboardButton;
	private int m_currentTotal;
	private TrueTime m_lastMinute;

	private void updateFields(PatternParser patternParser) {

		// step 1 of 2: update relevant counter field
		int timeSpent = patternParser.getTimeSpentOfficially();
		CounterField relevantCounterField = patternParser.getCounterField();
		relevantCounterField.add(timeSpent);
		try {
			int gapTimeSpent = patternParser.getStoppingTime();
			gapTimeSpent += patternParser.getProcrastinationTime();
			m_gapField.add(gapTimeSpent);
			// step 2 of 2: update general text area (saving version of line
			// that includes current total)
			if (relevantCounterField != m_gapField) {
				m_currentTotal += timeSpent; // update current total
			}
			m_events.add(patternParser.produceLineWithCurrentTotal(m_currentTotal,
			    m_gapField.getValue()));
			m_lastMinute = patternParser.getEndMinute();

		} catch (Exception e) {
			m_parsingErrors.add("Internal program error 1");
		}

	}

	private void parse(String line) {
		RegularActivityParser regularActivityParser = new RegularActivityParser(
		    line, m_lastMinute);
		BonusActivityParser bonusActivityParser = new BonusActivityParser(line,
		    m_lastMinute);
		if (regularActivityParser.doesMatch()) {
			updateFields(regularActivityParser);
		} else if (bonusActivityParser.doesMatch()) {
			updateFields(bonusActivityParser);
		} else {
			m_events.add(line);
			m_parsingErrors
			    .add(Utilities.EOL + "SYNTAX ERROR: DON'T UNDERSTAND '" + line + "'");
		}
	}

	// refactor idea: make fields into a list, also handy for creating layout
	void resetFields() {
		m_gapField.reset();
		m_wowField.reset();
		m_debtField.reset();
		m_maintenanceField.reset();
		m_workField.reset();
	}

	private void evaluate(String text) {
		StringReader stringReader = new StringReader(text);
		BufferedReader reader = new BufferedReader(stringReader);
		resetFields();
		m_parsingErrors.clear();
		m_events.clear();
		m_currentTotal = 0;
		m_lastMinute = new TrueTime(); // last minute is unknown
		try {
			String line;
			do {
				line = reader.readLine();
				parse(line);
			} while (line != null && line != "");
			reader.close();
		} catch (Exception e) {
		}
		m_writingPad.setText("");
		Iterator<String> lineIterator = m_events.iterator();
		while (lineIterator.hasNext()) {
			m_writingPad.append(lineIterator.next());
			if (lineIterator.hasNext()) {
				m_writingPad.append(Utilities.EOL);
			}
		}
		for (String error : m_parsingErrors) {
			m_writingPad.append(error);
		}
	}

	private void saveAndQuit() {
		try (FileWriter outputFileWriter = new FileWriter(saveFileName)) {
			outputFileWriter.write(m_writingPad.getText());
			outputFileWriter.close();
			System.out.println("Closing");
		} catch (Exception e) {
			System.out.println("Error1");
		}
		;
	}

	private void parseConfigLine

	private TimeKeeper() {
		super("TimeKeeper");
		setLayout(new GridBagLayout());
		m_writingPad = new JTextArea();
		try (FileReader inputFileReader = new FileReader(saveFileName)) {
			BufferedReader inputFileBReader = new BufferedReader(inputFileReader);
			String totalText = "";
			String currentLine = "";

			do {
				currentLine = inputFileBReader.readLine();
				if (currentLine != null) {
					totalText += currentLine + Utilities.EOL;
				}
			} while (currentLine != null && currentLine != "");
			m_writingPad.setText(totalText);

		} catch (Exception e) {
		}

		try (BufferedReader configurationFileBReader = new BufferedReader(
		    new FileReader(configurationFileName))) {
			String currentLine = "";
			do {
				currentLine = configurationFileBReader.readLine();
				parseConfigLine(currentLine);
			} while (currentLine != null && currentLine != "");
		} catch (Exception e) {

		}

		m_gapLabel = new JLabel("Gat"); // is special, will always be needed
		// and has no explicit initializers
		m_gapField = new CounterField();
		m_debtLabel = new JLabel("Opruimen");
		m_debtField = new CounterField();
		m_workLabel = new JLabel("Werk");
		m_workField = new CounterField();
		m_maintenanceLabel = new JLabel("Onderhoud");
		m_maintenanceField = new CounterField();
		m_wowLabel = new JLabel("WoW");
		m_wowField = new CounterField();
		m_copyToClipboardButton = new JButton("Copy to clipboard");
		m_parsingErrors = new ArrayList<>();
		m_events = new ArrayList<>();
		GridBagConstraints gridbagConstraints = new GridBagConstraints();
		gridbagConstraints.fill = GridBagConstraints.BOTH;
		gridbagConstraints.weightx = 1.0;
		gridbagConstraints.weighty = 4.0;
		setGridbagConstraintsDimension(gridbagConstraints, 0, 0, 5, 5);
		JScrollPane scrollPaneForWritingPad = new JScrollPane(m_writingPad);
		add(scrollPaneForWritingPad, gridbagConstraints);
		gridbagConstraints.weighty = 0.5;
		setGridbagConstraintsDimension(gridbagConstraints, 0, 5, 1, 1);
		add(m_gapLabel, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 0, 6, 1, 1);
		add(m_gapField, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 1, 5, 1, 1);
		add(m_debtLabel, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 1, 6, 1, 1);
		add(m_debtField, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 2, 5, 1, 1);
		add(m_workLabel, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 2, 6, 1, 1);
		add(m_workField, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 3, 5, 1, 1);
		add(m_maintenanceLabel, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 3, 6, 1, 1);
		add(m_maintenanceField, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 4, 5, 1, 1);
		add(m_wowLabel, gridbagConstraints);
		setGridbagConstraintsDimension(gridbagConstraints, 4, 6, 1, 1);
		add(m_wowField, gridbagConstraints);
		m_writingPad.addKeyListener(this);
		setGridbagConstraintsDimension(gridbagConstraints, 3, 7, 2, 1);
		add(m_copyToClipboardButton, gridbagConstraints);
		m_copyToClipboardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int registeredTime = m_currentTotal + m_gapField.getValue();
				int percentageTimeUsed = (int) (100 * m_currentTotal / registeredTime);
				String myString = m_gapField.getText() + " gat + "
		        + m_debtField.getText() + " opr + " + m_workField.getText()
		        + " werk + " + m_maintenanceField.getText() + " ondh + "
		        + m_wowField.getText() + " WoW => Admin: " + registeredTime
		        + ", Tot = " + m_currentTotal + " min (" + percentageTimeUsed + "%)"
		        + Utilities.EOL;
				StringSelection stringSelection = new StringSelection(myString);
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}

		});
		TimeCodeRepository.init(m_gapField, m_debtField, m_workField,
		    m_maintenanceField, m_wowField);
		setSize(500, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		evaluate(m_writingPad.getText());

	}

	private void setGridbagConstraintsDimension(
	    GridBagConstraints gridbagConstraints, int xpos, int ypos, int width,
	    int height) {
		gridbagConstraints.gridheight = height;
		gridbagConstraints.gridwidth = width;
		gridbagConstraints.gridx = xpos;
		gridbagConstraints.gridy = ypos;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			evaluate(m_writingPad.getText());
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String[] args) {
		TimeKeeper mainFrame = new TimeKeeper();
		mainFrame.addWindowListener(new WindowAdapter() {
			// WINDOW_CLOSING event handler
			@Override
			public void windowClosing(WindowEvent e) {

				mainFrame.saveAndQuit();
				System.out.println("Closing");
				super.windowClosing(e);
			}
		});
	}

}
