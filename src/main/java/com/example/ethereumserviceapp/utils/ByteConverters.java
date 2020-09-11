/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.ethereumserviceapp.utils;

import java.util.Collections;
import org.web3j.abi.datatypes.generated.Bytes16;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.utils.Numeric;

/**
 *
 * @author nikos
 */
public class ByteConverters {

    public static String asciiToHex16(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString() + "".join("", Collections.nCopies(16 - (hex.length() / 2), "00"));
    }

    public static String asciiToHex32(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length() / 2), "00"));
    }

    public static Bytes16 stringToByes16(String asciiString) {
        //Numeric.hexStringToByteArray(strValueInHex) converts any HexString to byte[].
        byte[] myStringInByte = Numeric.hexStringToByteArray(asciiToHex16(asciiString));
        return new Bytes16(myStringInByte);
    }

    public static Bytes32 stringToByes32(String asciiString) {
        //Numeric.hexStringToByteArray(strValueInHex) converts any HexString to byte[].
        byte[] myStringInByte = Numeric.hexStringToByteArray(asciiToHex32(asciiString));
        return new Bytes32(myStringInByte);
    }

    public static String hexToASCII(String hexValue) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2) {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

}
