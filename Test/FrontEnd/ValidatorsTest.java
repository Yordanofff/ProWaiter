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
    void isEmptyStringAValidName() {
        String testValue = "";
        assertFalse(Validators.isValidName(testValue));
    }

    @Test
    void isNullAValidName() {
        String testValue = null;
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
    void isStringWithSpaceAValidUsername() {
        String testValue = "user name";
        assertFalse(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isLowercaseLettersOnlyValidUsername() {
        String testValue = "username";
        assertTrue(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isEmptyStringAValidUserName() {
        String testValue = "";
        assertFalse(Validators.isValidUsername(testValue, 1));
    }

    @Test
    void isNullAValidUsername() {
        String testValue = null;
        assertFalse(Validators.isValidUsername(testValue,1));
    }

    @Test
    void isPasswordWithSpaceValid() {
        String testValue = "pass word";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isShortPasswordLongEnough() {
        String testValue = "pass";
        assertFalse(Validators.isPasswordValid(testValue, 10));
    }

    @Test
    void isPasswordWithoutLowercaseAValidPassword() {
        String testValue = "PASS123!";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isLongPasswordLongEnough() {
        String testValue = "passWord123!";
        assertTrue(Validators.isPasswordValid(testValue, 10));
    }

    @Test
    void isPasswordWithoutCapitalLetterValid() {
        String testValue = "password";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isPasswordOnlyWithCapitalLettersValid() {
        String testValue = "PASSWORD";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isPasswordWithCapitalAndLowercaseLettersValid() {
        String testValue = "passWORD";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isPasswordWithDigitsOnlyValid() {
        String testValue = "123";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isPasswordWithAllRequiredLettersAndDigitsButNotLongEnoughValid() {
        String testValue = "passWORD123";
        assertFalse(Validators.isPasswordValid(testValue, 20));
    }

    @Test
    void isPasswordWithAllRequiredLettersAndDigitsAndLongEnoughValid() {
        String testValue = "passWORD123";
        assertTrue(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isEmptyStringAValidPassword() {
        String testValue = "";
        assertFalse(Validators.isPasswordValid(testValue, 1));
    }

    @Test
    void isNullAValidPassword() {
        String testValue = null;
        assertFalse(Validators.isPasswordValid(testValue,1));
    }

    @Test
    void isCapitalLetterInStringWithCapitalLetter() {
        String input = "HelloWorld";
        assertTrue(Validators.isCapitalLetterInString(input));
    }

    @Test
    void isCapitalLetterInStringWithoutCapitalLetter() {
        String input = "helloworld";
        assertFalse(Validators.isCapitalLetterInString(input));
    }

    @Test
    void isCapitalLetterInStringWithEmptyString() {
        String input = "";
        assertFalse(Validators.isCapitalLetterInString(input));
    }

    @Test
    void isCapitalLetterInStringWithNullString() {
        String input = null;
        assertFalse(Validators.isCapitalLetterInString(input));
    }

    @Test
    void isNameWrittenCorrectlyWithNullString() {
        String input = null;
        assertFalse(Validators.isNameWrittenCorrectly(input));
    }

    @Test
    void isNameWrittenCorrectlyWithEmptyString() {
        String input = "";
        assertFalse(Validators.isNameWrittenCorrectly(input));
    }

    @Test
    void isNameWrittenCorrectlyWithNameStartingWithCapitalLetter() {
        String input = "Ivan";
        assertTrue(Validators.isNameWrittenCorrectly(input));
    }

    @Test
    void isNameWrittenCorrectlyWithLowercaseName() {
        String input = "ivan";
        assertFalse(Validators.isNameWrittenCorrectly(input));
    }

    @Test
    void isNameWrittenCorrectlyWithCapitalLettersOnlyName() {
        String input = "IVAN";
        assertFalse(Validators.isNameWrittenCorrectly(input));
    }


    // TODO - add more tests
}