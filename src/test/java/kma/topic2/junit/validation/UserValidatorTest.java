package kma.topic2.junit.validation;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    private static final String FULL_NAME = "Anton Kononko";
    private static final String LOGIN = "anton888";
    private static final String VALID_PASSWORD = "abcde1";

    private static final String EMPTY_PASSWORD = "";
    private static final String SHORT_PASSWORD = "ab";
    private static final String LONG_PASSWORD = "123456789";
    private static final String NOT_MATCHING_REGEX_PASSWORD = "її№;$";

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    private NewUser user;


    @BeforeEach
    void setUp() {
        Mockito.when(userRepository.isLoginExists(LOGIN)).thenReturn(false);

        user = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(VALID_PASSWORD)
                .build();
    }


    /*
     * New valid user should pass the validation and no errors should be thrown
     */
    @Test
    void shouldPassOnNewValidUser() {
        assertDoesNotThrow(() -> userValidator.validateNewUser(user));
    }


    /*
     * UserValidator should throw LoginExistsException when validating existing user
     */
    @Test
    void shouldNotPassOnExistingLogin() {
        // Override value from the setUp, because this test should definitely test such behaviour
        Mockito.when(userRepository.isLoginExists(LOGIN)).thenReturn(true);

        LoginExistsException exception = assertThrows(LoginExistsException.class, () -> userValidator.validateNewUser(user));
        checkExceptionMessage(exception, String.format("Login %s already taken", user.getLogin()));
    }


    /*
     * UserValidator should throw ConstraintViolationException when password is too short, too long, is empty
     * or when it does not match regex
     */
    @Test
    void shouldNotPassOnInvalidPasswords() {
        // empty password
        NewUser emptyPasswordUser = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(EMPTY_PASSWORD)
                .build();

        assertThrows(ConstraintViolationException.class, () -> userValidator.validateNewUser(emptyPasswordUser));

        // short password
        NewUser shortPasswordUser = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(SHORT_PASSWORD)
                .build();

        assertThrows(ConstraintViolationException.class, () -> userValidator.validateNewUser(shortPasswordUser));

        // long password
        NewUser longPasswordUser = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(LONG_PASSWORD)
                .build();

        assertThrows(ConstraintViolationException.class, () -> userValidator.validateNewUser(longPasswordUser));

        // password does not match the regex
        NewUser notMatchingRegexPasswordUser = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(NOT_MATCHING_REGEX_PASSWORD)
                .build();

        // exception message is always the same but it would be nice to test exception message at least once
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userValidator.validateNewUser(notMatchingRegexPasswordUser));
        checkExceptionMessage(exception, "You have errors in you object");
    }

    private void checkExceptionMessage(RuntimeException exception, String message) {
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}