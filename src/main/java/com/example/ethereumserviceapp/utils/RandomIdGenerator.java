/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.util.Random;

/**
 *
 * @author nikos
 */
public class RandomIdGenerator {

    private static char[] _base62chars
            = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                    .toCharArray();

    private static Random _random = new Random();

    public static String GetBase62(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(_base62chars[_random.nextInt(62)]);
        }
        return sb.toString();
    }

    public static String GetBase36(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(_base62chars[_random.nextInt(36)]);
        }
        return sb.toString();
    }
}
