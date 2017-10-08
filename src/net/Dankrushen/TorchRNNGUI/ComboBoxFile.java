package net.Dankrushen.TorchRNNGUI;

import java.io.File;

public class ComboBoxFile {
	File file;

	public ComboBoxFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		if (file != null)
			return TorchProject.getNameNoExtension((file.getName()));
		return "None";
	}
}
