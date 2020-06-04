package com.hummer.common.test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hummer.common.utils.ObjectCopyUtils;
import lombok.Data;
import org.junit.Test;

import java.util.List;

public class ObjectCopyUtilTest {
    @Test
    public void copy() {
        A a = new A();
        a.setA(456);
        List<B> bList = Lists.newArrayListWithCapacity(2);
        B b1 = new B();
        b1.setA("ssss");
        bList.add(b1);

        B b2 = new B();
        b2.setA("1111");
        bList.add(b2);

        a.setB(bList);

        D target = new D();

        ObjectCopyUtils.copy(a,target);
        target.setB(ObjectCopyUtils.copyByList(a.getB(),F.class));
        System.out.println(JSON.toJSONString(target));

        System.out.println(target.getB().get(0).getA());
    }

    @Data
    public class D {
        private Integer a;
        private List<F> b;
    }

    @Data
    public class A {
        private Integer a;
        private List<B> b;
    }

    @Data
    public class B {
        private String a;
    }

    @Data
    public static class F {
        private String a;
    }
}
