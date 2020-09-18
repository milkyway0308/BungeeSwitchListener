package skywolf46.bungeeswitchlistener.util.bytesupportstream;

import skywolf46.bungeeswitchlistener.util.bytesupportstream.exception.ByteStreamException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteSupportOutputStream implements DataOutput, AutoCloseable {
    private ByteArrayOutputStream bis = new ByteArrayOutputStream();
    private DataOutput dos = new DataOutputStream(bis);

    @Override
    public void write(int b) {
        try {
            dos.write(b);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            dos.write(b);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        try {
            dos.write(b, off, len);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeBoolean(boolean v) {
        try {
            dos.writeBoolean(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeByte(int v) {
        try {
            dos.writeByte(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeShort(int v) {
        try {
            dos.writeShort(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeChar(int v) {
        try {
            dos.writeChar(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeInt(int v) {
        try {
            dos.writeInt(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeLong(long v) {
        try {
            dos.writeLong(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeFloat(float v) {
        try {
            dos.writeFloat(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeDouble(double v) {
        try {
            dos.writeDouble(v);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeBytes(String s) {
        try {
            dos.writeBytes(s);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeChars(String s) {
        try {
            dos.writeChars(s);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void writeUTF(String s) {
        try {
            dos.writeUTF(s);
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    @Override
    public void close() {
        try {
            bis.close();
        } catch (IOException e) {
            throw new ByteStreamException(e);
        }
    }

    public byte[] toByteArray() {
        return bis.toByteArray();
    }

    public byte[] closeStream() {
        byte[] bx = bis.toByteArray();
        close();
        return bx;
    }

    public int size() {
        return bis.size();
    }
}
