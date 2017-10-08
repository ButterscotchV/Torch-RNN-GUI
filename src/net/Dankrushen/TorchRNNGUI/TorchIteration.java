package net.Dankrushen.TorchRNNGUI;

import java.util.Date;

public class TorchIteration {
	Date timeReceived;
	long currentDone = -1;
	long totalToDo = -1;
	long currentLeft = -1;
	double loss = -1;

	private boolean debug = false;

	public TorchIteration(String output) {
		timeReceived = new Date();
		parseConsoleOutput(output);
	}

	public long getCompleted() {
		return currentDone;
	}

	public long getLeft() {
		return currentLeft;
	}

	public double getLoss() {
		return loss;
	}

	public Date getTime() {
		return timeReceived;
	}

	public long getTotal() {
		return totalToDo;
	}

	public void parseConsoleOutput(String output) {
		if (debug)
			System.out.println("Output \"" + output + "\"");

		if (debug)
			System.out.println("Matches " + output.matches(
					"Epoch (\\d*\\.?\\d+) \\/ (\\d*\\.?\\d+), i = (\\d*\\.?\\d+) \\/ (\\d*\\.?\\d+), loss = (\\d*\\.?\\d+)\\s*"));

		if (output.matches(
				"Epoch (\\d*\\.?\\d+) \\/ (\\d*\\.?\\d+), i = (\\d*\\.?\\d+) \\/ (\\d*\\.?\\d+), loss = (\\d*\\.?\\d+)\\s*")) {
			String[] outputSplit = output.split(", (i|loss) = ");

			if (debug)
				System.out.println("Epoch " + outputSplit[0]);
			if (debug)
				System.out.println("Numbers " + outputSplit[1]);
			if (debug)
				System.out.println("Loss " + outputSplit[2]);

			String[] outputNumbers = outputSplit[1].split(" / ");

			currentDone = Long.valueOf(outputNumbers[0]);
			totalToDo = Long.valueOf(outputNumbers[1]);
			currentLeft = totalToDo - currentDone;

			String outputLoss = outputSplit[2].replaceAll("//s+", "");

			loss = Double.valueOf(outputLoss);
		}
	}

	public boolean validIteration() {
		if (timeReceived == null || currentDone == -1 || loss == -1 || totalToDo == -1 || currentLeft == -1)
			return false;

		return true;
	}
}
