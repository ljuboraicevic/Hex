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
            PlayerMonteCarlo pmc = new PlayerMonteCarlo(0,1);
            Class[] cArg = new Class[2];
            cArg[0] = Board.class;
            cArg[1] = byte.class;
            boolean[] solutions = {true, false, true, true, true, true, true, false, false};
            Method method = PlayerMonteCarlo.class.getDeclaredMethod("didIWin", cArg);
            method.setAccessible(true);

            for (int i = 1; i < 10; i++) {
                String filename = "testInputFiles/Table/tableTest" + i + ".txt";
                Board t = new Board(filename);
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
    
    @Test
    public void testGetNumberOfFirstPlayersMoves() {
        System.out.println("Testing getNumberOfFirstPlayersMoves");
        try {
            //private method, so we need to use reflection
            Method method = PlayerMonteCarlo.class.getDeclaredMethod(
                    "getNumberOfFirstPlayersMoves",
                    Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            
            PlayerMonteCarlo pmc = new PlayerMonteCarlo(0,1);
            //int movesPlayed, int tableSize
            
            int res1 = (int) method.invoke(pmc, 1, 25);
            int res2 = (int) method.invoke(pmc, 2, 25);
            int res3 = (int) method.invoke(pmc, 2, 16);
            int res4 = (int) method.invoke(pmc, 1, 16);
            int res5 = (int) method.invoke(pmc, 11, 25);
            int res6 = (int) method.invoke(pmc, 10, 25);
            int res7 = (int) method.invoke(pmc, 10, 16);
            int res8 = (int) method.invoke(pmc, 0, 16);
            
            assertEquals(12, res1);
            assertEquals(11, res2);
            assertEquals(6, res3);
            assertEquals(7, res4);
            assertEquals(7, res5);
            assertEquals(7, res6);
            assertEquals(2, res7);
            assertEquals(7, res8);
            
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

    @Test
    public void testGetSequence() {
        System.out.println("Testing getSequence");
        //private method, so we need to use reflection
        Method method;
        try {
            method = PlayerMonteCarlo.class.getDeclaredMethod(
                    "getSequence",
                    Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            //int movesPlayed, int tableSize
            
            PlayerMonteCarlo pmc = new PlayerMonteCarlo(0,1);
            
            byte[] res1 = (byte[])method.invoke(pmc, 0, 9);
            byte[] res2 = (byte[])method.invoke(pmc, 1, 16);
            byte[] res3 = (byte[])method.invoke(pmc, 1, 9);
            byte[] res4 = (byte[])method.invoke(pmc, 0, 16);
            
            assertArrayEquals(new byte[] {1,1,1,1,2,2,2,2}, res1);
            assertArrayEquals(new byte[] {1,1,1,1,1,1,1,2,2,2,2,2,2,2}, res2);
            assertArrayEquals(new byte[] {1,1,1,1,2,2,2}, res3);
            assertArrayEquals(new byte[] {1,1,1,1,1,1,1,2,2,2,2,2,2,2,2}, res4);
            
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
