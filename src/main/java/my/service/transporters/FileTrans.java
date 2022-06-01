package my.service.transporters;

import my.service.entities.File;

import java.io.InputStream;

public class FileTrans {

    private File file;

    private byte[] byteArray;

    public FileTrans(File file, byte[] byteArray) {
        this.file = file;
        this.byteArray = byteArray;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }
}
