package com.example.research2slidesweb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class MPFGUI implements MultipartFile {
	private final byte[] fileBytes;
    private final String fileName;
    private final String contentType;

    public MPFGUI(byte[] fileBytes, String fileName, String contentType) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileBytes.length == 0;
    }

    @Override
    public long getSize() {
        return fileBytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileBytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileBytes);
    }

    @Override 
    public void transferTo(File dest) throws IOException, IllegalStateException {
        transferTo(dest.toPath());
    }
}
