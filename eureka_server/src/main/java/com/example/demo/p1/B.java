package com.example.demo.p1;

public class B  extends A {

    StringBuilder bd = new StringBuilder("hihi");
    public void eat() {
        System.out.println("B eat");
    }
    public static  void main(String args) {
        B b = new B();
        System.out.println(b.bd);
//        b.bd
    }
}
