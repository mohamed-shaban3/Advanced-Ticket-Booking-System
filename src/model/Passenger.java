package model;

public class Passenger extends Person {

    private int passengerId;

    public Passenger(String name, int age, int passengerId) {
        super(name, age);
        this.passengerId = passengerId;
    }

    @Override
    public void printInfo() {
        System.out.println("Passenger: " + name + ", Age: " + age + ", ID: " + passengerId);
    }

    public int getPassengerId() {
        return passengerId;
    }
}
