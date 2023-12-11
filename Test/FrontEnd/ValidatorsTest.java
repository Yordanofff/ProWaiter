package FrontEnd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorsTest {
    @Test
    void isStringAValidName() {
        String testValue = "Peter";
        assertTrue(Validators.isValidName(testValue));
    }

    @Test
    void isIntegerAValidName() {
        String testValue = "2";
        assertFalse(Validators.isValidName(testValue));
    }

    @Test
    void isNameWithSpacesAValidName() {
        String testValue = "Peter Pan";
        assertFalse(Validators.isValidName(testValue));
    }

    @Test
    void isStringWithExclamationSignValidUsername() {
        String testValue = "username!";
        assertFalse(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isStringWithDashSignValidUsername() {
        String testValue = "username-";
        assertFalse(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isStringWithUnderscoreSignValidUsername() {
        String testValue = "username_";
        assertTrue(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isUsernameLongEnoughWithRequiredLength1() {
        String testValue = "username_";
        assertTrue(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isUsernameLongEnoughWithRequiredLength15() {
        String testValue = "username_";
        assertFalse(Validators.isValidUsername(testValue, 15));
    }

    @Test
    void isCapitalLetterValidUsername() {
        String testValue = "Username_";
        assertTrue(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isCapitalLettersOnlyValidUsername() {
        String testValue = "USERNAME";
        assertTrue(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isLowercaseLettersOnlyValidUsername() {
        String testValue = "username";
        assertTrue(Validators.isValidUsername(testValue, 1));
    }

    // TODO - add more tests
}