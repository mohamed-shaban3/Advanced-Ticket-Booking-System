package model;


public class Person implements Printable {
    protected String name;
    protected int age;

    // Constructor Overloading
    public Person() {
        this.name = "Unknown";
        this.age = 0;
    }

    public Person(String name) {
        this.name = name;
        this.age = 0;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public void printInfo() {
        System.out.println("Name: " + name + ", Age: " + age);
    }
}
