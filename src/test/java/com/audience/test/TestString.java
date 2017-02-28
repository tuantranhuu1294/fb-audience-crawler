package com.audience.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huutuan on 03/02/2017.
 */
public class TestString {

    @Test
    public void test(){

        String s1 = "Software Engineer";
        String s2 = "Thing 1";

        System.out.println(getLessWordSort(s1, s2));

        List<String> arr = new ArrayList<>();
        arr.add("a");
        arr.add("b");
        arr.add("c");
        System.out.println(arr.get(0));
    }

    private String getLessWordSort(String str1, String str2) {
        if (str1.equalsIgnoreCase(str2))
            return str1;

        char[] charArr1 = str1.toCharArray();
        char[] charArr2 = str2.toCharArray();

        int i = 0;
        while (true) {
            if (charArr1.length < i + 1)
                return str1;
            if (charArr2.length < i + 1)
                return str2;

            if (charArr1[i] < charArr2[i])
                return str1;
            else if (charArr2[i] < charArr1[i])
                return str2;

            i++;
        }
    }
}
