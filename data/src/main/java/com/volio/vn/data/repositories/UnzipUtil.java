package com.volio.vn.data.repositories;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class UnzipUtil {
    public static String rajDhaniSuperFastUnzip(String inputZipFile, String destinationDirectory) {
        String pathFile = "";
        try {
            int BUFFER = 2048;
            List<String> zipFiles = new ArrayList<String>();
            File sourceZipFile = new File(inputZipFile);
            File unzipDestinationDirectory = new File(destinationDirectory);
            unzipDestinationDirectory.mkdir();
            ZipFile zipFile;
            zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
            Enumeration zipFileEntries = zipFile.entries();
            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(unzipDestinationDirectory, currentEntry);

                try {
                    ensureZipPathSafety(destFile, unzipDestinationDirectory.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (currentEntry.endsWith(".zip")) {
                    zipFiles.add(destFile.getAbsolutePath());
                }

                File destinationParent = destFile.getParentFile();

                destinationParent.mkdirs();

                if (pathFile.equals("") || pathFile.length() > destinationParent.getPath().length()) {
                    pathFile = destinationParent.getPath();
                }

                try {
                    if (!entry.isDirectory()) {
                        BufferedInputStream is =
                                new BufferedInputStream(zipFile.getInputStream(entry));
                        int currentByte;
                        byte data[] = new byte[BUFFER];

                        FileOutputStream fos = new FileOutputStream(destFile);
                        BufferedOutputStream dest =
                                new BufferedOutputStream(fos, BUFFER);
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                        dest.close();
                        is.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            zipFile.close();

            for (Iterator<String> iter = zipFiles.iterator(); iter.hasNext(); ) {
                String zipName = (String) iter.next();
                unzip(
                        zipName,
                        destinationDirectory +
                                File.separatorChar +
                                zipName.substring(0, zipName.lastIndexOf(".zip"))
                );
                String name = destinationDirectory +
                        File.separatorChar +
                        zipName.substring(0, zipName.lastIndexOf(".zip"));
                Log.d("Naxx0z2", "rajDhaniSuperFastUnzip: " + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return pathFile;
    }

    private static final int BUFFER = 2048;

    public static void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                //Check close
                if (fi != null) {
                    fi.close();
                }
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fix policy Play Console
     * https://support.google.com/faqs/answer/9294009
     * https://stackoverflow.com/questions/56303842/fixing-a-zip-path-traversal-vulnerability-in-android
     *
     * @param outputFile
     * @param destDirectory
     * @throws Exception
     */
    private static void ensureZipPathSafety(final File outputFile, final String destDirectory) throws Exception {
        String destDirCanonicalPath = (new File(destDirectory)).getCanonicalPath();
        String outputFileCanonicalPath = outputFile.getCanonicalPath();
        if (!outputFileCanonicalPath.startsWith(destDirCanonicalPath)) {
            throw new Exception(String.format("Found Zip Path Traversal Vulnerability with %s", outputFileCanonicalPath));
        }
    }

    public static void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        dirChecker(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                try {
                    File outputFile = new File(_targetLocation, ze.getName());
                    ensureZipPathSafety(outputFile, _targetLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(new File(_targetLocation, ze.getName()));
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
            if (fin != null) {
                fin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dirChecker(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}