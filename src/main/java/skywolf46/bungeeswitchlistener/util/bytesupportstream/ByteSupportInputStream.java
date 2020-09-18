package skywolf46.bungeeswitchlistener.util.bytesupportstream;


import skywolf46.bungeeswitchlistener.util.bytesupportstream.exception.ByteStreamException;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class ByteSupportInputStream implements DataInput, AutoCloseable {
    private ByteArrayInputStream bis;
    private DataInputStream dis;

    public ByteSupportInputStream(byte[] arr) {
        this.bis = new ByteArrayInputStream(arr);
        this.dis = new DataInputStream(bis);
    }

    @Override
    public void readFully(byte[] b) {
        try {
            dis.readFully(b);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) {
        try {
            dis.readFully(b, off, len);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public int skipBytes(int n) {
        try {
            return dis.skipBytes(n);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public boolean readBoolean() {
        try {
            return dis.readBoolean();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public byte readByte() {
        try {
            return dis.readByte();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public int readUnsignedByte() {
        try {
            return dis.readUnsignedByte();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return dis.readShort();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public int readUnsignedShort() {
        try {
            return dis.readUnsignedShort();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public char readChar() {
        try {
            return dis.readChar();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return dis.readInt();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public long readLong() {
        try {
            return dis.readLong();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public float readFloat() {
        try {
            return dis.readFloat();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public double readDouble() {
        try {
            return dis.readDouble();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public String readLine() {
        try {
            return dis.readLine();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public String readUTF() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void close() {
        try {
            dis.close();
            bis.close();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }
}
