package com.hummer.api.web;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/11/6 15:05
 **/
public class OtherTest {
    public static void main(String[] args){
        Thread.currentThread().interrupt();
        System.out.printf("-----------%s", Thread.currentThread().getState());
    }
}
