package com.course.hsa;

import java.util.Random;

public class TestApp {

    public static void main(String[] args) {
        int i=0;
        while(i < 100) {
            System.out.println("Value: " + Math.abs(Math.log(Math.random())));
            i++;
        }
    }

}
