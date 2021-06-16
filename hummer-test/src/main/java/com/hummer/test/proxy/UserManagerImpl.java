package com.hummer.test.proxy;

public class UserManagerImpl implements UserManager {
    @Override
    public void showName(String name) {
        System.out.println("hello " + name);
    }
}
