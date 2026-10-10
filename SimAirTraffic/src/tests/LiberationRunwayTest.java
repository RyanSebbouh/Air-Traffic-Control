package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
/**
 * Unit test of the liberation of a runway.
 * @author MGHAIETH Faycal
 *
 */
public class LiberationRunwayTest {
	
	private boolean liberationlaunched = false;
	
	private MainGUIFreePisteTest launcher;
	
	public void setLibLaunched(Boolean value) {
		liberationlaunched = value;
	}
	
	@Before 
	public void prepareMainGUI() {
		launcher = new MainGUIFreePisteTest("Aerien game", this);        
        
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
		assertEquals(true, liberationlaunched);

    }

}
