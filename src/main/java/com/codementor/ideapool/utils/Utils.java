package com.codementor.ideapool.utils;

import java.util.Random;

public class Utils {
    private static final int REFRESH_TOKEN_LENGTH = 100;

    private static final int DEFAULT_RANDOM_ID_LENGTH = 9;

    private static final char[] ID_CHARS = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();

    public static String generateRefreshToken() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < REFRESH_TOKEN_LENGTH) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, REFRESH_TOKEN_LENGTH);
    }

    public static String generateRandomId() {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < DEFAULT_RANDOM_ID_LENGTH; i++) {
            char c = ID_CHARS[r.nextInt(ID_CHARS.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
