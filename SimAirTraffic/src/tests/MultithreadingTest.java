package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * The multithreading unit test. It checks if a plane has waited for an area to become free.
 * @author MGHAIETH Faycal
 *
 */
public class MultithreadingTest {
	
	private boolean hasWaited = false;
	
	private MainGUITest launcher;
	
	public void setWaited(Boolean value) {
		hasWaited = value;
	}
	
	@Before 
	public void prepareMainGUI() {
		launcher = new MainGUITest("Aerien game", this);        
        
	}
	
	@Test
    public void testRun() {
        Thread thread = new Thread(launcher);
        thread.start();

        //When the thread is finish...
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //... Let's test !
		assertEquals(true, hasWaited);

    }
	

}
