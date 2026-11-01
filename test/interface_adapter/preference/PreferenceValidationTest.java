package interface_adapter.preference;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PreferenceValidationTest {
    @Test void zeroMaximumAllowsUnboundedAgeAndErrorsClearAfterCorrection() {
        PreferenceViewModel model = new PreferenceViewModel();
        model.getState().setMinAge("-1");
        assertFalse(model.validatePreferences());
        model.getState().setMinAge(" 3 ");
        model.getState().setMaxAge("0");
        assertTrue(model.validatePreferences());
        assertEquals("", model.getState().getMinAgeError());
        model.getState().setMaxAge("2");
        assertFalse(model.validatePreferences());
    }
}
