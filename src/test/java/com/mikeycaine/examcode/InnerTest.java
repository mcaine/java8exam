package com.mikeycaine.examcode;

import org.junit.Test;

public class InnerTest {

    interface Touchable {
        void touch();
    }

    class Outer {
        int val = 420;

        public void inc() {
            val += 1;
        }

        private Touchable touchable = new Touchable() {

            int total = 69;
            public void touch() {
                System.out.println("Val is " + val + ", total is " + total);
            }
        };

        public Touchable getTouchable() {
            return touchable;
        }
    }

    @Test
    public void testIt() {
        Outer outer = new Outer();
        outer.getTouchable().touch(); // Val is 420, total is 69
        outer.inc();
        outer.getTouchable().touch(); // Val is 421, total is 69
    }
}
