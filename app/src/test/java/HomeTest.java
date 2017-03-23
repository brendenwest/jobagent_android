/**
 * Created by brenden on 9/11/16.
 */

import com.brisksoft.jobagent.Home;

import org.junit.Test;
import static org.junit.Assert.*;

public class HomeTest {

    private Home home = new Home();
    @Test
    public void validSearchEntries_ReturnsTrue() {
        assertTrue(home.validSearchEntries("sample search", "98104"));
        assertFalse(home.validSearchEntries("", "location"));
        assertFalse(home.validSearchEntries("sample search", ""));
    }

}