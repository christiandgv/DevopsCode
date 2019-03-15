package test;

import org.junit.Test;

import static org.junit.Assert.*;

public class CallTestControllerJunit {
    @Test
    public void suma() {
        assertEquals(40, Integer.sum(20, 20));
    }

    @Test
    public void persona() {
        fail("Fallo");
    }

}
