package darep.repos;

import java.io.File;
import java.io.IOException;

import darep.Helper;
import darep.storage.DataSet;
import darep.storage.Metadata;
import darep.storage.StorageException;

class RepositoryDataSet implements DataSet {
	
	private Metadata metaData;
	
	private File file;
	
	private boolean copyMode;
	
	void setCopyMode(boolean copyMode) {
		this.copyMode =  copyMode;
	}
	
	void setMetadata(Metadata m) {
		this.metaData = m;
	}
	
	void setFile(File f) {
		this.file = f;
	}

	@Override
	public Metadata getMetadata() {
		return metaData;
	}

	@Override
	public void copyFileTo(File path) throws StorageException {
		if (this.copyMode) {
			try {
				Helper.copyRecursive(file, path);
			} catch (IOException e) {
				throw new StorageException("Could not copy file " + file +
											" to path " + path, e);
			}
		} else {
			file.renameTo(path);
		}
	}

	@Override
	public Type getType() {
		if (file.exists()) {
			return file.isDirectory() ? DataSet.Type.folder : DataSet.Type.file;
		} else {
			return null;
		}
	}

}
