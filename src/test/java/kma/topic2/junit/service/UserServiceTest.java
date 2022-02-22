package kma.topic2.junit.service;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceTest {

    private static final String FULL_NAME = "Anton Kononko";
    private static final String LOGIN = "anton888";
    private static final String VALID_PASSWORD = "abcde1";

    private static final String INVALID_PASSWORD = "ab";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private NewUser newUser;
    private User user;

    @BeforeEach
    void setUp() {
        newUser = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(VALID_PASSWORD)
                .build();

        user = User.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(VALID_PASSWORD)
                .build();
    }

    /*
     * It should create new user and UserService should return created user by login
     */
    @Test
    void shouldCreateUserAndFindItByLogin() {
        userService.createNewUser(newUser);
        assertThat(userService.getUserByLogin(LOGIN)).isEqualTo(user);
    }


    /*
     * It should not create a new user if user with the same login is already exists
     */
    @Test
    void shouldNotCreateExistingUser() {
        assertThrows(LoginExistsException.class, () -> userService.createNewUser(newUser));
    }


    /*
     * It should not create user with invalid params and throw a ConstraintViolationException
     * UserService should not find user if it was not created
     */
    @Test
    void shouldNotCreateInvalidUserAndNotFindCreatedUserByLogin() {
        NewUser invalidUser = NewUser.builder()
                .fullName(FULL_NAME)
                .login(LOGIN)
                .password(INVALID_PASSWORD)
                .build();

        assertThrows(ConstraintViolationException.class, () -> userService.createNewUser(invalidUser));
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserByLogin(LOGIN));
    }


}