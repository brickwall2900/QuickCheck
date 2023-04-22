package com.github.brickwall2900.quickcheck;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuickCheck {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: quickcheck [/h:\"(algorithm 1),(algorithm 2)\" (optional)] [inFile]");
            throw new IllegalArgumentException("Missing arguments");
        }

        List<String> files = new ArrayList<>(Arrays.asList(args));
        List<String> algorithms = new ArrayList<>();
        Optional<String> extraAlgorithms = files.stream().filter(s -> s.startsWith("/h:")).findFirst();
        extraAlgorithms.ifPresent(s -> {
            parseAlgorithms(algorithms, s);
            files.remove(s);
        });

        algorithms.add("SHA-1");
        algorithms.add("SHA-256");
        algorithms.add("MD5");

        for (String file : files) {
            File inFile = new File(file);
            System.out.println("File: -> " + inFile.getAbsolutePath());

            if (inFile.isDirectory()) {
                System.err.println("File is a directory");
                continue;
            }

            if (!inFile.exists()) {
                System.err.println("File does not exist");
                continue;
            }

            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
            for (String digestName : algorithms) {
                service.execute(() -> {
                    try {
                        byte[] buffer = new byte[8192 * 2];
                        int count;
                        MessageDigest digest = MessageDigest.getInstance(digestName);
                        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(inFile.toPath()))) {
                            while ((count = bis.read(buffer)) > 0) {
                                digest.update(buffer, 0, count);
                            }
                            byte[] hash = digest.digest();
                            String hashString = bytesToHex(hash);
                            System.out.printf("%s: %s%n", digestName, hashString);
                        } catch (IOException e) {
                            System.err.printf("%s: IO Exception%n", digestName);
                            e.printStackTrace();
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        System.err.printf("%s: UNSUPPORTED%n", digestName);
                    }
                });
            }
            service.shutdown();
            if (!service.awaitTermination(96, TimeUnit.HOURS)) System.err.println("Timed out!");
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press ENTER key to continue...");
        scanner.nextLine();
        System.exit(0);
    }

    private static void parseAlgorithms(List<String> algorithmsOut, String arg) {
        arg = arg.replace("/h:", ""); // "/h:" replacement to empty string
        String[] algorithmsParse = arg.split(","); // split with ','
        if (algorithmsParse.length > 0) {
            List<String> algorithmList = new ArrayList<>(Arrays.asList(algorithmsParse));
            algorithmList.removeIf(String::isBlank); // remove blank entries
            if (algorithmList.size() > 0) {
                System.out.printf("Found extra algorithms: %s%n", algorithmList);
                algorithmsOut.addAll(algorithmList); // add all
            }
        }
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
