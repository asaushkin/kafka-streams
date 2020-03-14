package utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class SplitWordsTest {
    @Test
    public void stringWithComma() {
        String[] result = SplitWords.toWords("a,b,c");
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }
    @Test
    public void stringWithNoComma() {
        String[] result = SplitWords.toWords("a");
        assertEquals(1, result.length);
        assertEquals("a", result[0]);
    }
    @Test
    public void emptyString() {
        String[] result = SplitWords.toWords("");
        assertNull(result);
    }
    @Test
    public void nullString() {
        String[] result = SplitWords.toWords(null);
        assertNull(result);
    }
}
