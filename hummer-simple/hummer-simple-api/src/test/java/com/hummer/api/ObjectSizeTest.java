package com.hummer.api;

import lombok.Data;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

public class ObjectSizeTest {

    @Test
    public void lockUpFlow() throws InterruptedException {
        Object1 object1 = new Object1();

        System.out.println(ClassLayout.parseInstance(object1).toPrintable());
        Thread.sleep(2000);

        synchronized (object1) {
            System.out.println(ClassLayout.parseInstance(object1).toPrintable());
        }
    }

    @Test
    public void biasLock() throws InterruptedException {
        //System.out.println(ClassLayout.parseInstance(new Object1()).toPrintable());
        Thread.sleep(3000);
        System.out.println(ClassLayout.parseInstance(new Object1()).toPrintable());
    }

    @Test
    public void outputObjectSize() throws InterruptedException {
        final Object lockObjA = new Object();
        final Object lockObjB = new Object();
        final Object1 object1 = new Object1();

        Thread threadA = new Thread(() -> {
            synchronized (lockObjA) {
                System.out.println("-------------------------------thead1.0----------------");
                System.out.println(ClassLayout.parseInstance(new Object1()).toPrintable());
                try {
                    Thread.sleep(3000);
                    synchronized (lockObjB) {
                        System.out.println("-------------------------------thead1.1----------------");
                        System.out.println(ClassLayout.parseInstance(new Object1()).toPrintable());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            synchronized (lockObjB) {
                System.out.println("-------------------------------thead2.0----------------");
                System.out.println(ClassLayout.parseInstance(new Object1()).toPrintable());
                try {
                    Thread.sleep(3000);
                    synchronized (lockObjA) {
                        System.out.println("-------------------------------thead2.1----------------");
                        System.out.println(ClassLayout.parseInstance(new Object1()).toPrintable());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        threadB.start();
        threadA.start();

        threadB.join();
        threadA.join();

        //System.out.println("---------------------------");
        //System.out.println(RamUsageEstimator.shallowSizeOf(new Object1()));
        //System.out.println("---------------------------");
        //System.out.println(RamUsageEstimator.sizeOfObject(new Object1()));
    }

    private static class ObjectC {
        ObjectD[] array = new ObjectD[2];

        public ObjectC() {
            array[0] = new ObjectD();
            array[1] = new ObjectD();
        }
    }

    private static class ObjectD {
        int value;
    }

    @Data
    static class Object1 {
        private Long aLong;
        private Integer a;
        private Object2[] object2s = new Object2[]{new Object2(), new Object2()};
    }


    @Data
    static class Object2 {
        private Integer i;
    }
}
