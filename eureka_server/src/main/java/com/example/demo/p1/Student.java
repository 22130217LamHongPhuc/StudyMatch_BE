package com.example.demo.p1;

import java.util.Map;
import java.util.TreeMap;

public class Student implements Comparable<Student>{
    int id;
    String name;
    int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Student(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Student o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public static void main(String[] args) {
        Student s1 =new Student(1, "Tai", 20);
        Student s2 =new Student(2, "Tai 2", 40);
        Map<Integer, Student> map = new TreeMap<>();
        map.put(1, s2);
        map.put(2, s1);
        System.out.println(map.toString());
        System.out.println(s1.compareTo(s2));
    }
}
