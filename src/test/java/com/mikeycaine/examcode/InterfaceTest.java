package com.mikeycaine.examcode;

import org.junit.Test;

public class InterfaceTest {


    abstract interface MyAbstractInterface {
        public void myMethod();
    }

    interface MyInterface {
        public void run();
    }

    interface MyOtherInterface {
        public void run();
    }

    class Cat implements MyInterface, MyOtherInterface {
        @Override
        public void run() {

        }
    }

    @Test
    public void testCanCreateCat() {
        Cat cat = new Cat();
    }

    interface Something {
        default public void run() {
            System.out.println("Something");
        }
    }

    interface SomethingElse {
        default public void run() {
            System.out.println("SomethingElse");
        }
    }

    interface SomethingNotDefault {
        public void run();
    }

    // wont compile
    //class Budgie implements Something, SomethingElse {   }
    //class Giraffe implements Something, SomethingNotDefault { }
    //abstract class Lion implements Something, SomethingNotDefault { }
    //interface Another extends Something, SomethingElse {}
    //interface YetAnother extends Something, SomethingNotDefault {}

    class Dog implements Something, SomethingElse {

        @Override
        public void run() {
            SomethingElse.super.run();
        }
    }

    @Test
    public void testSuper() {
        Dog dog = new Dog();
        dog.run();
    }

    interface Red {
        public void stuff();
    }

    interface Green {
        public void stuff();
    }

    class GreenThing implements Green {
        public void stuff() {
            System.out.println("I'm green");
        }
    }

    class Coloured extends GreenThing implements Red {}

    @Test
    public void testColoured() {
        Red thing = new Coloured();
        Green greenThing = (Green)thing;

        thing.stuff();
        greenThing.stuff();
    }

    @Test
    public void testMore() {

        // Interface not allowed here
        //interface Animal {
        //    public void speak();
        //}


    }
}
