package net.Dankrushen.TorchRNNGUI;

import java.io.File;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class ProjectListener {
	public static void printFileInfo(Path path) {
		try {
			println(String.format("File %s, size %d, modified %s", path, Files.size(path),
					Files.getLastModifiedTime(path)));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void println(String str) {
		System.out.println(str);
	}

	public ProjectListener(final String path, final TorchRNNGUI gui) {
		File file = new File(path);
		file.mkdirs();
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// define a folder root
					Path pathObject = Paths.get(path);
					final WatchService service = pathObject.getFileSystem().newWatchService();
					WatchKey key = pathObject.register(service, StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					try {
						while (true) {
							int s = service.take().pollEvents().size();
							for (int i = 0; i < s; i++) {
								gui.makeProjectList();
							};
							
							boolean validKey = key.reset();

							if (!validKey) {
								println("Invalid key");
								break; // infinite for loop
							}
						}
					} catch (ClosedWatchServiceException e) {
						println("Service closed");
					}
				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					println("Watcher thread exiting");
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
}
