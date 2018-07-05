package ru.jevil.profitest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test(expected = NullPointerException.class)
    public void addition_isNotCorrect() throws Exception {
        String str = null;
        assertTrue(str.isEmpty());
    }
}