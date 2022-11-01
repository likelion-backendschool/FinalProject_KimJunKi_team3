package com.ll.finalproject.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Ut {


    public static class url {
        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }
    }
    public String nf(int number) {
        return String.format("%,d", number);
    }
}

