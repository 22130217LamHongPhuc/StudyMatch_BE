package com.example.demo.p1;

import java.util.Objects;

public class b {
    String name ;

    public b(String name) {
        this.name = name;
    }



    public static void main(String[] args) {
        b b = new b("hello");
        b c = new b("hello");
        System.out.println(b.equals(c));
    }
}
