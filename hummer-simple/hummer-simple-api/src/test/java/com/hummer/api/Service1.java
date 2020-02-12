package com.hummer.api;

import com.hummer.core.SpringApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class Service1 {
    @Autowired
    private RefService1 refService1;

    @Lookup
    public RefService1 getRefService1() {
        return null;
    }

    public void hell() {
        getRefService1().showMessage();
    }


    public void hell2() {
        //从容器中获取，每次都创建个实例
        SpringApplicationContext.getBean(RefService1.class).showMessage();
    }
}
