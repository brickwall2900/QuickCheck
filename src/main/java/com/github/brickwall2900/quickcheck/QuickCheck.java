package com.github.brickwall2900.quickcheck;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.Scanner;

public class QuickCheck {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: quickcheck [inFile]");
            throw new IllegalArgumentException("Missing arguments");
        }
        String[] files = args[0].split(";");
        for (String file : files) {
            File inFile = new File(file);
            System.out.println("File: -> " + inFile.getAbsolutePath());
            if (inFile.isDirectory()) {
                throw new IllegalArgumentException("File is a directory");
            }
            if (!inFile.exists()) {
                inFile = new File(System.getProperty("user.dir") + File.separatorChar + args[0]);
                System.out.println("File: -> " + System.getProperty("user.dir") + File.separatorChar + args[0]);
                if (inFile.isDirectory()) {
                    throw new IllegalArgumentException("File is a directory");
                }
                if (!inFile.exists()) {
                    throw new FileNotFoundException("File does not exist");
                }
            }
            byte[] buffer = new byte[8192];
            int count;
            MessageDigest[] digests = new MessageDigest[]{
                    MessageDigest.getInstance("SHA-256"),
                    MessageDigest.getInstance("SHA-1"),
                    MessageDigest.getInstance("MD5")
            };
            for (MessageDigest digest : digests) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile))) {
                    System.out.print(digest.getAlgorithm() + ": ");
                    while ((count = bis.read(buffer)) > 0) {
                        digest.update(buffer, 0, count);
                    }
                    byte[] hash = digest.digest();
                    String hashString = bytesToHex(hash);
                    System.out.println(hashString);
                }
            }
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press ENTER key to continue...");
        scanner.nextLine();
        System.exit(0);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
