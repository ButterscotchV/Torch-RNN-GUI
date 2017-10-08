package net.Dankrushen.TorchRNNGUI;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import org.apache.commons.io.comparator.NameFileComparator;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

public class TorchRNNGUI {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TorchRNNGUI window = new TorchRNNGUI();
					window.frmTorchGUI.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JFrame frmTorchGUI;
	private JButton btnTrain;
	private JComboBox<ComboBoxFile> comboBoxInput;
	private JComboBox<ComboBoxFile> comboBoxCheckpointInput;

	private JLabel lblInputFile;
	private JLabel lblResumeFile;

	private DefaultComboBoxModel<ComboBoxFile> comboBoxProject = new DefaultComboBoxModel<ComboBoxFile>();
	private DefaultComboBoxModel<ComboBoxFile> comboBoxCheckpoint = new DefaultComboBoxModel<ComboBoxFile>();

	private String mainFolder = new File("").getAbsolutePath() + "/";
	private JProgressBar progressBar;
	private TorchProject selectedProject;
	private TorchProgressManager latestManager;
	private JLabel lblNumbers;

	private JLabel lblLoss;
	private JLabel lblTimeLeft;
	private JButton btnSample;
	private JSpinner spinnerLength;
	private JSpinner spinnerTemperature;
	private JLabel lblIterationsTitle;
	private JLabel lblLossTitle;
	private JLabel lblTimeRemainingTitle;
	private JLabel lblLengthTitle;
	private JLabel lblTemperatureTitle;
	private JSeparator separator;
	private JSeparator separator_1;
	private JLabel lblTrainingMultiplierTitle;
	private JSpinner spinnerTrainingMultiplier;
	private JLabel lblModelLayersTitle;
	private JSpinner spinnerModelLayers;
	private JButton btnClear;
	private JLabel lblSampleText;
	private JScrollPane scrollPane;
	private JTextArea txtStartingText;
	private ChartPanel chartPanel;

	/**
	 * Create the application.
	 */
	public TorchRNNGUI() {
		initialize();
	}

	public void buttonSampleEnabled() {
		if (btnSample != null) {
			if (selectedProject != null)
				btnSample.setEnabled(selectedProject.getCheckpointFile() != null);
			else
				btnSample.setEnabled(false);
		}
	}

	public TorchProject getSelectedProject() {
		return selectedProject;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTorchGUI = new JFrame();
		frmTorchGUI.setTitle("Torch-RNN Easy GUI");
		// frmTorchGUI.setUndecorated(true);
		frmTorchGUI.setBounds(100, 100, 450, 725);
		frmTorchGUI.setMinimumSize(frmTorchGUI.getSize());
		frmTorchGUI.setLocationRelativeTo(null);
		frmTorchGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FormLayout formLayout = new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:7dlu:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:7dlu:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow(10)"),
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow"),
						FormSpecs.RELATED_GAP_ROWSPEC,});
		formLayout.setColumnGroups(new int[][]{new int[]{2, 4}});
		frmTorchGUI.getContentPane().setLayout(formLayout);

		makeProjectList();

		lblInputFile = new JLabel("Input File");
		lblInputFile.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblInputFile, "2, 2, 3, 1, center, center");

		comboBoxInput = new JComboBox<ComboBoxFile>();
		comboBoxInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (latestManager != null)
					latestManager.cleanUp();
				makeCheckpointList();
			}
		});
		comboBoxInput.setModel(comboBoxProject);
		frmTorchGUI.getContentPane().add(comboBoxInput, "2, 4, 3, 1, fill, fill");

		makeCheckpointList();

		lblResumeFile = new JLabel("Checkpoint File");
		lblResumeFile.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblResumeFile, "2, 6, 3, 1, fill, fill");

		comboBoxCheckpointInput = new JComboBox<ComboBoxFile>();
		comboBoxCheckpointInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxCheckpointInput.getSelectedItem() != null) {
					selectedProject
					.setCheckpointFile(((ComboBoxFile) comboBoxCheckpointInput.getSelectedItem()).getFile());
				} else {
					selectedProject.setCheckpointFile(null);
				}

				buttonSampleEnabled();
			}
		});
		comboBoxCheckpointInput.setModel(comboBoxCheckpoint);
		frmTorchGUI.getContentPane().add(comboBoxCheckpointInput, "2, 8, 3, 1, fill, fill");

		btnTrain = new JButton("Train");
		btnTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnTrain.getText().equals("Train")) {
					btnTrain.setText("Stop");
					setInterfaceEnabled(false);
					new Thread() {
						@Override
						public void run() {
							if (latestManager != null)
								latestManager.cleanUp();

							latestManager = new TorchProgressManager(selectedProject, progressBar, chartPanel, lblNumbers, lblLoss,
									lblTimeLeft);
							btnTrain.setEnabled(true);
							selectedProject.train(latestManager, (Double) spinnerTrainingMultiplier.getValue(),
									(Integer) spinnerModelLayers.getValue());
							setInterfaceEnabled(true);
							btnTrain.setEnabled(true);
							btnTrain.setText("Train");
							makeCheckpointList();
						}
					}.start();
				} else {
					if (latestManager != null) {
						latestManager.shouldKill(true);
					}

					btnTrain.setEnabled(false);
				}
			}
		});

		btnClear = new JButton("Remove Project Data");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(null,
						"Are you sure you would like to remove all data related to this project?", "Warning",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (selectedProject != null)
						selectedProject.removeFolders();
					makeCheckpointList();
				}
			}
		});
		frmTorchGUI.getContentPane().add(btnClear, "2, 10, 3, 1, fill, fill");

		separator = new JSeparator();
		frmTorchGUI.getContentPane().add(separator, "2, 12, 3, 1, fill, center");

		lblTrainingMultiplierTitle = new JLabel("Training Multiplier");
		lblTrainingMultiplierTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblTrainingMultiplierTitle, "2, 14, fill, fill");

		lblModelLayersTitle = new JLabel("Model Layers");
		lblModelLayersTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblModelLayersTitle, "4, 14");

		spinnerTrainingMultiplier = new JSpinner();
		spinnerTrainingMultiplier
		.setModel(new SpinnerNumberModel(new Double(1), new Double(0.1), null, new Double(0.1)));
		frmTorchGUI.getContentPane().add(spinnerTrainingMultiplier, "2, 16, fill, fill");

		spinnerModelLayers = new JSpinner();
		spinnerModelLayers.setModel(new SpinnerNumberModel(new Integer(2), new Integer(1), null, new Integer(1)));
		frmTorchGUI.getContentPane().add(spinnerModelLayers, "4, 16");
		frmTorchGUI.getContentPane().add(btnTrain, "2, 18, 3, 1, fill, fill");

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		frmTorchGUI.getContentPane().add(progressBar, "2, 20, 3, 1, fill, fill");

		lblIterationsTitle = new JLabel("Iterations");
		lblIterationsTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblIterationsTitle, "2, 22, fill, fill");

		lblLossTitle = new JLabel("Loss");
		lblLossTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblLossTitle, "4, 22, fill, fill");

		lblNumbers = new JLabel("0/0");
		lblNumbers.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblNumbers, "2, 24, fill, fill");

		lblLoss = new JLabel("Unknown");
		lblLoss.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblLoss, "4, 24, fill, fill");

		lblTimeRemainingTitle = new JLabel("Time Remaining");
		lblTimeRemainingTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblTimeRemainingTitle, "2, 26, 3, 1, fill, fill");

		lblTimeLeft = new JLabel("Unknown");
		lblTimeLeft.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblTimeLeft, "2, 28, 3, 1, fill, fill");

		btnSample = new JButton("Sample");
		btnSample.setEnabled(false);
		btnSample.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					@Override
					public void run() {
						setInterfaceEnabled(false);
						selectedProject.sample((Long) spinnerLength.getValue(), (Double) spinnerTemperature.getValue(), txtStartingText.getText());
						setInterfaceEnabled(true);
					}
				}.start();
			}
		});

		JFreeChart lineChart = ChartFactory.createLineChart(
				null,
				"","Loss",
				null,
				PlotOrientation.VERTICAL,
				true,true,false);

		CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
		CategoryAxis domain = plot.getDomainAxis();
		domain.setVisible(false);

		chartPanel = new ChartPanel(lineChart);

		frmTorchGUI.getContentPane().add(chartPanel, "2, 30, 3, 1, fill, fill");

		separator_1 = new JSeparator();
		frmTorchGUI.getContentPane().add(separator_1, "2, 32, 3, 1, fill, center");

		lblSampleText = new JLabel("Starting Text");
		lblSampleText.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblSampleText, "2, 38, 3, 1");

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frmTorchGUI.getContentPane().add(scrollPane, "2, 40, 3, 1, fill, fill");

		txtStartingText = new JTextArea();
		scrollPane.setViewportView(txtStartingText);
		frmTorchGUI.getContentPane().add(btnSample, "2, 42, 3, 1, fill, fill");

		lblLengthTitle = new JLabel("Length (In Characters)");
		lblLengthTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblLengthTitle, "2, 34, fill, fill");

		lblTemperatureTitle = new JLabel("Temperature");
		lblTemperatureTitle.setHorizontalAlignment(SwingConstants.CENTER);
		frmTorchGUI.getContentPane().add(lblTemperatureTitle, "4, 34, fill, fill");

		spinnerLength = new JSpinner();
		spinnerLength.setModel(new SpinnerNumberModel(new Long(2000), new Long(0), null, new Long(1)));
		frmTorchGUI.getContentPane().add(spinnerLength, "2, 36, fill, fill");

		spinnerTemperature = new JSpinner();
		spinnerTemperature.setModel(new SpinnerNumberModel(0.6, 0.0, 1.0, 0.01));
		frmTorchGUI.getContentPane().add(spinnerTemperature, "4, 36, fill, fill");

		new ProjectListener(mainFolder + "data", this);
	}

	public void makeCheckpointList() {
		if(comboBoxInput.getSelectedItem() != null) {
			File selectedFile = ((ComboBoxFile) comboBoxInput.getSelectedItem()).getFile();
			selectedProject = new TorchProject(selectedFile, mainFolder);

			File[] checkpoints = selectedProject.getCheckpointList();

			comboBoxCheckpoint.removeAllElements();

			for (File f : checkpoints) {
				comboBoxCheckpoint.addElement(new ComboBoxFile(f));
			}

			if (comboBoxCheckpoint.getSize() > 1)
				comboBoxCheckpoint.setSelectedItem(comboBoxCheckpoint.getElementAt(1));
		}

		buttonSampleEnabled();
	}

	public void makeProjectList() {
		File[] projects = new File(mainFolder + "data").listFiles();

		comboBoxProject = new DefaultComboBoxModel<ComboBoxFile>();

		if(projects != null && projects.length > 0) {
			Arrays.sort(projects, NameFileComparator.NAME_COMPARATOR);

			for (File f : projects) {
				if (!f.isDirectory() && f.getName().toLowerCase().endsWith(".txt")) {
					comboBoxProject.addElement(new ComboBoxFile(f));

					if (selectedProject != null && f.equals(selectedProject.getMainFile()))
						comboBoxProject.setSelectedItem(new ComboBoxFile(f));
				}
			}
		}

		if (comboBoxInput != null) {
			comboBoxInput.setModel(comboBoxProject);
		}
	}

	public void setInterfaceEnabled(boolean enabled) {
		for (Component c : frmTorchGUI.getContentPane().getComponents()) {
			if (!(c instanceof JLabel) && !(c instanceof JProgressBar))
				c.setEnabled(enabled);
		}
		txtStartingText.setEnabled(enabled);
	}
}