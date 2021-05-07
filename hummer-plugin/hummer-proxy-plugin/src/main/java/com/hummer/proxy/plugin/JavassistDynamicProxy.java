package com.hummer.proxy.plugin;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author lee
 */
public class JavassistDynamicProxy {

    private final String targetFullClassName;
    private final String targetMethod;

    public JavassistDynamicProxy(String targetFullClassName, String targetMethod) {
        this.targetFullClassName = targetFullClassName;
        this.targetMethod = targetMethod;
    }

    public Object insertBeforeAndAfter(String beforeMethodSrc, String afterMethodSrc)
            throws CannotCompileException, NotFoundException, IllegalAccessException, InstantiationException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(targetFullClassName);
        CtMethod m = cc.getDeclaredMethod(targetMethod);
        m.insertBefore(beforeMethodSrc);
        m.insertAfter(afterMethodSrc);
        Class c = cc.toClass();
        return c.newInstance();
    }


    public Object insertBefore(String methodSrc)
            throws CannotCompileException, NotFoundException, IllegalAccessException, InstantiationException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(targetFullClassName);
        CtMethod m = cc.getDeclaredMethod(targetMethod);
        m.insertBefore(methodSrc);
        Class c = cc.toClass();
        return c.newInstance();
    }

    public Object insertAfter(String methodSrc) throws CannotCompileException, InstantiationException
            , NotFoundException, IllegalAccessException {
        return insertAfter(methodSrc, false);
    }

    public Object insertAfter(String methodSrc, boolean asFinally)
            throws CannotCompileException, NotFoundException, IllegalAccessException, InstantiationException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(targetFullClassName);
        CtMethod m = cc.getDeclaredMethod(targetMethod);
        m.insertAfter(methodSrc, asFinally);
        Class c = cc.toClass();
        return c.newInstance();
    }
}
