package game;
/**
 * Auto Test for the all
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GameControllerTest.class,
        GameModelTest.class,
        PlayerTest.class
})
public class AutoTest {
}
