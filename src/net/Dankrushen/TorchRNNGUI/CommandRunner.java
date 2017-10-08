package net.Dankrushen.TorchRNNGUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandRunner {
	public static void execute(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			while (true) {
				String line1 = br1.readLine();
				String line2 = (br2.ready() ? br2.readLine() : null);

				if (line1 == null && line2 == null)
					break;

				if (line1 != null && !line1.isEmpty())
					System.out.println(line1);
				if (line2 != null && !line2.isEmpty())
					System.out.println(line2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void execute(TorchProgressManager manager, String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			while (true) {
				if(manager.shouldKill)
					process.destroyForcibly();
				
				String line1 = br1.readLine();
				String line2 = (br2.ready() ? br2.readLine() : null);

				if (line1 == null && line2 == null)
					break;

				if (line1 != null && !line1.isEmpty()) {
					System.out.println(line1);

					TorchIteration iteration = new TorchIteration(line1);
					if (iteration.validIteration())
						manager.addIteration(iteration);
				}
				if (line2 != null && !line2.isEmpty())
					System.out.println(line2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}