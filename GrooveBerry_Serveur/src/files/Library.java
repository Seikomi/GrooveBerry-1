package files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Library extends TrackStorage {
	public static final String DEFAULT_PATHNAME = "res/library.xml";

	public Library() {
		super(DEFAULT_PATHNAME);
	}
	
	public Library(ArrayList<AudioFile> audioFileList) throws IOException {
		this();
		this.audioFileList = new ArrayList<>(audioFileList);
		this.updateLibraryFile();
	}

	@Override
	public void add(String filePath) throws FileNotFoundException {
		if (!this.contains(filePath)) {
			super.add(filePath);
		} else {
			System.out.println("Fichier deja present dans la bibliotheque"); //avoid track duplication
		}
	}
}