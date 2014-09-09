/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nikola
 */
public class PlayerMonteCarloTest {

    public PlayerMonteCarloTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of makeMove method, of class PlayerMonteCarlo.
     */
    @Test
    public void testDidIWin() {
        try {
            PlayerMonteCarlo pmc = new PlayerMonteCarlo();
            Class[] cArg = new Class[2];
            cArg[0] = Table.class;
            cArg[1] = byte.class;
            boolean[] solutions = {true, false, true, true, true, true, true, false, false};
            Method method = PlayerMonteCarlo.class.getDeclaredMethod("didIWin", cArg);
            method.setAccessible(true);

            for (int i = 1; i < 10; i++) {
                String filename = "testInputFiles/Table/tableTest" + i + ".txt";
                Table t = new Table(filename);
                assertEquals("tableTest" + i + ".txt: won player \"1\": " + solutions[i - 1], solutions[i - 1], method.invoke(pmc, t, (byte) 0));
                assertEquals("tableTest" + i + ".txt: won player \"2\": " + !solutions[i - 1], !solutions[i - 1], method.invoke(pmc, t, (byte) 1));
            }

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PlayerMonteCarloTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PlayerMonteCarloTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PlayerMonteCarloTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PlayerMonteCarloTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PlayerMonteCarloTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
