package net.Dankrushen.TorchRNNGUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TorchProject {
	private static List<File> fileArrayToList(File[] array) {
		List<File> list = new ArrayList<File>();
		for (int i = 0; i < array.length; i++) {
			list.add(i, array[i]);
		}
		return list;
	}

	private static File[] fileListToArray(List<File> list) {
		File[] array = new File[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static String getNameNoExtension(String name) {
		return replaceLast(name, "\\..+", "");
	}

	private static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

	File mainFile;
	File checkpoint;

	String mainFileName;

	String baseFolder;

	public TorchProject(File mainFile, String baseFolder) {
		this.mainFile = mainFile;
		this.baseFolder = baseFolder;

		mainFileName = getNameNoExtension(mainFile.getName());
	}

	public void deleteFolder(File folder) {
		if (folder != null && folder.exists()) {
			for (File f : folder.listFiles())
				f.delete();

			folder.delete();
		}
	}

	public File getCheckpointFile() {
		return checkpoint;
	}

	public File[] getCheckpointList() {
		File projectFolder = new File(baseFolder + "cv/" + mainFileName);
		List<File> fileList = new ArrayList<File>();

		if(projectFolder.exists()) {

			File[] checkpointFiles = projectFolder.listFiles();

			for (File f : checkpointFiles) {
				if (!f.isDirectory() && f.getName().toLowerCase().endsWith(".t7"))
					fileList.add(f);
			}

			fileList = fileArrayToList(sortByNumber(fileListToArray(fileList)));
			
			fileList.add(null);

			Collections.reverse(fileList);
		} else fileList.add(null);

		return fileListToArray(fileList);
	}

	public File getMainFile() {
		return mainFile;
	}

	public void makeFolders() {
		File sampleFolder = new File(baseFolder + "samples/" + mainFileName);
		if(!sampleFolder.exists()) sampleFolder.mkdirs();

		File folder = new File(baseFolder + "processeddata/" + mainFileName);
		if(!folder.exists())  folder.mkdirs();

		File projectFolder = new File(baseFolder + "cv/" + mainFileName);
		if(!projectFolder.exists())  projectFolder.mkdirs();
	}

	public void preprocess() {
		makeFolders();

		CommandRunner.execute(
				"/bin/bash PreProcess.sh " + replaceStuff(mainFile.getAbsolutePath()) + " " + replaceStuff(mainFileName) + " " + replaceStuff(baseFolder));
	}

	public void removeCheckpoints() {
		for (File f : getCheckpointList()) {
			if (f != null)
				if (f.exists())
					f.delete();
		}
	}

	public void removeFolders() {
		File sampleFolder = new File(baseFolder + "samples/" + mainFileName);
		deleteFolder(sampleFolder);

		File dataFolder = new File(baseFolder + "processeddata/" + mainFileName);
		deleteFolder(dataFolder);

		File checkpointFolder = new File(baseFolder + "cv/" + mainFileName);
		removeCheckpoints();
		deleteFolder(checkpointFolder);
	}

	public void sample(long length, double temperature, String startingText) {
		makeFolders();

		if (checkpoint == null) {
			System.out.println("Error, no checkpoint selected");
			return;
		}

		File sampleFolder = new File(baseFolder + "samples/" + mainFileName);
		
		CommandRunner.execute("/bin/bash Sample.sh " + replaceStuff(mainFileName) + " " + replaceStuff(checkpoint.getName()) + " " + length + " "
				+ temperature + " " + replaceStuff(startingText) + " " + sampleFolder.listFiles().length + " " + replaceStuff(baseFolder));
	}

	public void setCheckpointFile(File checkpoint) {
		this.checkpoint = checkpoint;
	}

	public File[] sortByNumber(File[] array) {
		Arrays.sort(array, new Comparator<File>() {
			public int compare(File o1, File o2) {
				int n1 = extractNumber(o1.getName());
				int n2 = extractNumber(o2.getName());
				return n1 - n2;
			}

			private int extractNumber(String name) {
				int i = 0;
				try {
					int s = name.indexOf('_') + 1;
					int e = name.lastIndexOf('.');
					String number = name.substring(s, e);
					i = Integer.parseInt(number);
				} catch (Exception e) {
					i = 0; // if filename does not match the format
					// then default to 0
				}
				return i;
			}
		});

		return array;
	}

	public void train(TorchProgressManager manager, double trainingMultiplier, int modelLayers) {
		makeFolders();

		long epochNumber = Math.round(trainingMultiplier * 50);

		if (checkpoint == null) {
			removeFolders();
			makeFolders();
			preprocess();

			CommandRunner.execute(manager,
					"/bin/bash Train.sh " + replaceStuff(mainFileName) + " " + epochNumber + " " + modelLayers + " " + replaceStuff(baseFolder));
		} else {
			CommandRunner.execute(manager, "/bin/bash ResumeTrain.sh " + replaceStuff(mainFileName) + " " + replaceStuff(checkpoint.getName()) + " "
					+ epochNumber + " " + modelLayers + " " + replaceStuff(baseFolder));
		}
	}
	
	public String replaceStuff(String input) {
		input = input.replaceAll(" ", "&nbsp;");
		input = input.replaceAll("\\n", "&nl;");
		input = input.replaceAll("\\t", "&t;");
		return input;
	}
}
