package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import be.vinci.pae.domain.user.User;
import be.vinci.pae.domain.user.UserUCC;
import be.vinci.pae.exceptions.BusinessException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.Config;

public class UserUCCTest {

  private static UserUCC userUCC;
  private static User goodUser;
  private static User goodUserNotValidated;
  private static String goodEmail;
  private static String badEmail;
  private static String goodPassword;
  private static String badPassword;
  private static String goodEmailNotValidated;
  private static UserDAO userDAO;
  private static DalServices dalServices;

  /**
   * Initialisation before every tests.
   */
  @BeforeAll
  public static void init() {
    Config.load();
    badEmail = "test.test@test.com";
    badPassword = "5678";

    goodUser = UserDistributor.getGoodValidatedUser();
    goodPassword = "1234";
    goodEmail = goodUser.getEmail();
    goodUserNotValidated = UserDistributor.getGoodNotValidatedUser();
    goodEmailNotValidated = goodUserNotValidated.getEmail();

    ServiceLocator locator =
        ServiceLocatorUtilities.bind(new ApplicationBinder(), new ApplicationBinderTest());
    userUCC = locator.getService(UserUCC.class);

    userDAO = locator.getService(UserDAO.class);

    dalServices = locator.getService(DalServices.class);
  }

  @BeforeEach
  public void reset() {
    Mockito.reset(userDAO);
  }

  @DisplayName("Test connection with right email and password")
  @Test
  public void connectionTest1() {
    Mockito.when(userDAO.getUserFromEmail(goodEmail)).thenReturn(goodUser);
    assertEquals(goodUser, userUCC.connection(goodEmail, goodPassword));
  }

  @DisplayName("Test connection with bad email")
  @Test
  public void connectionTest2() {
    Mockito.when(userDAO.getUserFromEmail(badEmail)).thenReturn(null);
    assertThrows(UnauthorizedException.class, () -> userUCC.connection(badEmail, goodPassword));
  }

  @DisplayName("Test connection with bad password")
  @Test
  public void connectionTest3() {
    Mockito.when(userDAO.getUserFromEmail(goodEmail)).thenReturn(goodUser);
    assertThrows(UnauthorizedException.class, () -> userUCC.connection(goodEmail, badPassword));
  }

  @DisplayName("Test connection with empty email")
  @Test
  public void connectionTest4() {
    Mockito.when(userDAO.getUserFromEmail("")).thenReturn(null);
    assertThrows(UnauthorizedException.class, () -> userUCC.connection("", goodPassword));
  }

  @DisplayName("Test connection with empty password")
  @Test
  public void connectionTest5() {
    Mockito.when(userDAO.getUserFromEmail(goodEmail)).thenReturn(goodUser);
    assertThrows(UnauthorizedException.class, () -> userUCC.connection(goodEmail, ""));
  }

  @DisplayName("Test connection with empty email and empty password")
  @Test
  public void connectionTest6() {
    Mockito.when(userDAO.getUserFromEmail("")).thenReturn(null);
    assertThrows(UnauthorizedException.class, () -> userUCC.connection("", ""));
  }

  @DisplayName("Test connection with good credentials but the user isn't validated yet")
  @Test
  public void connectionTest7() {
    Mockito.when(userDAO.getUserFromEmail(goodEmailNotValidated)).thenReturn(goodUserNotValidated);
    assertThrows(UnauthorizedException.class,
        () -> userUCC.connection(goodEmailNotValidated, goodPassword));
  }

  @DisplayName("Test getting user from id with invalid id")
  @Test
  public void getUserFromIdTest1() {
    assertThrows(BusinessException.class, () -> userUCC.getUserFromId(-5));
  }

  @DisplayName("Test getting user from id with valid id but no user has this id")
  @Test
  public void getUserFromIdTest2() {
    int id = 55;
    Mockito.when(userDAO.getUserFromId(id)).thenReturn(null);
    assertThrows(BusinessException.class, () -> userUCC.getUserFromId(id));
  }

  @DisplayName("Test getting user from id with valid id")
  @Test
  public void getUserFromIdTest3() {
    int id = 1;
    Mockito.when(userDAO.getUserFromId(id)).thenReturn(goodUser);
    assertEquals(goodUser, userUCC.getUserFromId(id));
  }


}
