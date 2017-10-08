package net.Dankrushen.TorchRNNGUI;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class TorchProgressManager {
	private static double doubleArrayToDouble(double[] array) {
		double num = 0;
		for (double d : array) {
			num += d;
		}
		return num / array.length;
	}

	private static double[] doubleListToArray(List<Double> list) {
		double[] array = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	private static double doubleListToDouble(List<Double> list) {
		double num = 0;
		for (double d : list) {
			num += d;
		}
		return num / list.size();
	}

	private static String formatMillisIntoHumanReadable(long time) {
		long MILLISECONDS = 1000;
		long SECONDS = 60;
		long MINUTES = 60;

		time /= MILLISECONDS;
		int seconds = (int) (time % SECONDS);
		time /= SECONDS;
		int minutes = (int) (time % MINUTES);
		time /= MINUTES;
		int hours = (int) (time % 24);
		int days = (int) (time / 24);
		if (days == 0) {
			return String.format("%d Hours, %02d Minutes, %02d Seconds", hours, minutes, seconds);
		} else {
			return String.format("%,d Days, %02d Hours, %02d Minutes, %02d Seconds", days, hours, minutes, seconds);
		}
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	TorchProject project;
	JProgressBar progressBar;
	ChartPanel chartPanel;
	JLabel lblNumbers;

	JLabel lblLoss;

	JLabel lblTimeLeft;

	List<TorchIteration> iterations = new ArrayList<TorchIteration>();

	boolean shouldKill = false;

	double outlierFilterThreshold = 1.15;

	int maxIterations = 200;

	int maxLossIterations = 10; // maxLossIterations MUST be less than
	// maxIterations, but must be more than 0

	public TorchProgressManager(TorchProject project, JProgressBar progressBar, ChartPanel chartPanel, JLabel lblNumbers, JLabel lblLoss,
			JLabel lblTimeLeft) {
		this.project = project;
		this.progressBar = progressBar;
		this.chartPanel = chartPanel;
		this.lblNumbers = lblNumbers;
		this.lblLoss = lblLoss;
		this.lblTimeLeft = lblTimeLeft;
	}

	public void addIteration(TorchIteration iteration) {
		iterations.add(0, iteration);
		while (iterations.size() > maxIterations)
			iterations.remove(maxIterations);

		displayStats();
	}

	public String calculateLoss() {
		double loss = 0;
		for (int i = 0; i < maxLossIterations && i < iterations.size(); i++) {
			loss += iterations.get(i).getLoss();
		}

		if (iterations.size() < maxLossIterations)
			loss = loss / iterations.size();
		else
			loss = loss / maxLossIterations;

		DecimalFormat df = new DecimalFormat("#.##");

		return df.format(loss);
	}

	public void cleanUp() {
		shouldKill = false;

		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setMaximum(100);

		lblNumbers.setText("0/0");
		lblLoss.setText("Unknown");
		lblTimeLeft.setText("Unknown");

		iterations.clear();
	}

	public void displayStats() {
		TorchIteration iteration = getLatestIteration();

		long currentDone = iteration.getCompleted();
		long totalToDo = iteration.getTotal();
		long currentLeft = iteration.getLeft();

		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setMaximum((int) totalToDo);

		progressBar.setValue((int) currentDone);

		lblNumbers.setText(currentDone + "/" + totalToDo);
		lblLoss.setText(calculateLoss());
		lblTimeLeft.setText(getTimeLeft(currentLeft));

		JFreeChart lineChart = ChartFactory.createLineChart(
				null,
				"Iterations","Loss",
				createDataset(),
				PlotOrientation.VERTICAL,
				false,false,false);
		
		CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
		CategoryAxis domain = plot.getDomainAxis();
		domain.setVisible(false);

		chartPanel.setChart(lineChart);
	}

	private DefaultCategoryDataset createDataset( ) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

		for (int i = iterations.size() - 1; i >= 0; i--) {
			TorchIteration iteration = iterations.get(i);
			if (iteration != null) dataset.addValue(iteration.getLoss(), "loss", String.valueOf(iteration.getCompleted()));
		}

		return dataset;
	}

	public double[] filterOutliers(double[] someArray) {
		if (someArray.length > 0) {
			// Copy the values, rather than operating on references to existing
			// values
			double[] values = someArray.clone();

			// Then sort
			Arrays.sort(values);

			/*
			 * Then find a generous IQR. This is generous because if
			 * (values.length / 4) is not an int, then really you should average
			 * the two elements on either side to find q1.
			 */
			double q1 = values[(int) Math.floor((values.length / 4))];
			// Likewise for q3.
			double q3 = values[(int) Math.ceil((values.length * (3 / 4)))];
			double iqr = q3 - q1;

			// Then find min and max values
			double maxValue = q3 + iqr * outlierFilterThreshold;
			double minValue = q1 - iqr * outlierFilterThreshold;

			// Then filter anything beyond or beneath these values.
			List<Double> filteredValues = new ArrayList<Double>();
			for (double value : values) {
				if ((value <= maxValue) && (value >= minValue))
					filteredValues.add(value);
			}

			// Then return
			return doubleListToArray(filteredValues);
		} else
			return new double[] { 0 };
	}

	public double[] filterOutliers(List<Double> someList) {
		return filterOutliers(doubleListToArray(someList));
	}

	public TorchIteration getLatestIteration() {
		return iterations.get(0);
	}

	public double getMedian(double[] someArray) {
		if (someArray.length > 0) {
			// Copy the values, rather than operating on references to existing
			// values
			double[] values = someArray.clone();

			// Then sort
			Arrays.sort(values);

			return values[((int) Math.floor(values.length / 2))];
		} else
			return 0;
	}

	public double getMedian(List<Double> someList) {
		return getMedian(doubleListToArray(someList));
	}

	public String getTimeLeft(long iterationsLeft) {
		boolean debug = false;

		List<Double> timeBetween = new ArrayList<Double>();
		for (int i = iterations.size() - 1; i > 0; i--) {
			TorchIteration iteration1 = iterations.get(i);
			TorchIteration iteration2 = iterations.get(i - 1);

			timeBetween.add((double) getDateDiff(iteration1.getTime(), iteration2.getTime(), TimeUnit.MILLISECONDS));
		}

		double averageWithOutliers = doubleListToDouble(timeBetween);
		if (debug)
			System.out.println("Normal averaging " + averageWithOutliers);

		double averageNoOutliers = doubleArrayToDouble(filterOutliers(timeBetween));
		if (debug)
			System.out.println("Averaging without outliers " + averageNoOutliers);

		double averageMedian = getMedian(timeBetween);
		if (debug)
			System.out.println("Averaging using median " + averageMedian);

		double averageToUse = (Double.isNaN(averageMedian) || averageMedian <= 0
				? (Double.isNaN(averageNoOutliers) || averageNoOutliers <= 0 ? averageWithOutliers : averageNoOutliers)
						: averageMedian);

		// System.out.print(timeBetween);

		return formatMillisIntoHumanReadable(Math.round(averageToUse * getLatestIteration().getLeft()));
	}

	public boolean shouldKill() {
		return shouldKill;
	}

	public void shouldKill(boolean shouldKill) {
		this.shouldKill = shouldKill;
	}
}
