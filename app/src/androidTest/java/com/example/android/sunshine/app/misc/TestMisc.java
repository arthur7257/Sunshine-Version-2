package com.example.android.sunshine.app.misc;

import android.test.AndroidTestCase;

/**
 * Created by arturo.ayala on 5/27/16.
 */
public class TestMisc extends AndroidTestCase {

    public void testMyStack() {

    }

    public void testHeight() {
        Node root = new Node("A");
        root.left = new Node("B");
        root.right = new Node("C");


    }

    public static class Node {
        private String data;
        private Node left;
        private Node right;

        public Node(String data) {
            this.data = data;
        }
    }
}
