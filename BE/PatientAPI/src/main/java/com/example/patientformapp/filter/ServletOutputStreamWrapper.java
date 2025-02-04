package com.example.patientformapp.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServletOutputStreamWrapper extends ServletOutputStream {

    private ByteArrayOutputStream outputStream;
    private ServletOutputStream originalOutputStream;

    public ServletOutputStreamWrapper(ByteArrayOutputStream outputStream, ServletOutputStream originalOutputStream) {
        this.outputStream = outputStream;
        this.originalOutputStream = originalOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        originalOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        originalOutputStream.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        originalOutputStream.write(b);
    }

    @Override
    public boolean isReady() {
        return originalOutputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        originalOutputStream.setWriteListener(writeListener);
    }
}