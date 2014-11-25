package com.coursera.wfernandes.dailyselfie;

import java.io.File;
import java.io.FileFilter;

public class SelfieFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        return file.getName().contains("SELFIE_");
    }
}
