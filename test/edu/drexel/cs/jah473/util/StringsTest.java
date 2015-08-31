package edu.drexel.cs.jah473.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {
    @Test
    public void lastCharTest() {
        assertEquals('d', Strings.lastChar("word"));
    }

    @Test
    public void popTest() {
        StringBuilder sb = new StringBuilder("word");
        Strings.pop(sb);
        assertEquals("wor", sb.toString());
        StringBuffer sb2 = new StringBuffer("word");
        Strings.pop(sb2);
        assertEquals("wor", sb2.toString());
    }

    @Test
    public void toInitCapsTest() {
        String str = "my full name";
        String capped = Strings.toInitCaps(str);
        assertEquals("My Full Name", capped);
    }

    @Test
    public void reverseTest() {
        String str = "hello";
        String rev = Strings.reverse(str);
        assertEquals("olleh", rev);
    }

    @Test
    public void removePunctuationTest() {
        String str = "lots of, punctuation?!";
        String removed = Strings.removePunctuation(str);
        assertEquals("lots of punctuation", removed);
    }

    @Test
    public void fromCharacterArrayTest() {
        Character[] c = { 'a', 'b', 'c' };
        String str = Strings.fromCharacterArray(c);
        assertEquals("abc", str);
    }
}
