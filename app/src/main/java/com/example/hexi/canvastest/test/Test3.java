package com.example.hexi.canvastest.test;

/**
 * Created by hexi on 2017/7/2.
 */

public class Test3 {
    public static void main(String[] args) {
        System.out.println("-------第1次实例化-------");
        new C();
        System.out.println("-------第2次实例化-------");
        new C();
    }
}

class A {
    public A(){
        System.out.println("构造静态属性A");
    }
}
class B {
    public B(){
        System.out.println("构造普通属性B");
    }
}
class E {
    public E(){
        System.out.println("父类的静态属性E");
    }
}
class F {
    public F(){
        System.out.println("父类的普通属性F");
    }
}
class D {
    static E e = new E();
    F f = new F();
    public D(){
        System.out.println("构造父类D");
    }
}

class C extends D{

    public C(){
        System.out.println("构造函数C");
    }

    /**
     * 静态代码块先于构造器执行 普通块先于构造块  只执行一次
     * 凡是静态的与对象无关，先于对象存在的; 凡是静态的都是共享的
     */



    {
        System.out.println("普通代码块");
    }

    B b = new B();

    static {
        System.out.println("静态代码块");
    }

    static A a = new A();
}