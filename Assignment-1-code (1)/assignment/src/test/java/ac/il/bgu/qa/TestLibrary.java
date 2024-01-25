package ac.il.bgu.qa;

import ac.il.bgu.qa.errors.*;
import ac.il.bgu.qa.services.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestLibrary {

    @Mock
    DatabaseService mockDataBase;
    @Mock
    NotificationService mockNotification;
    @Mock
    ReviewService mockReview;

    @Mock
    Book mockBook;

    @Mock
    User mockUser;

    Library library;

    String validBookISBN = "9780306406157";
    String invalidBookISBN = "1234567891234";
    String validUserId = "123456789000";
    String invalidUserId = "12345678900013";
    String validBookTitle = "War and Peace";
    String invalidBookTitle = "";
    String validAuthor = "Leo Tolstoy";
    String invalidAuthor = "Leo Messi10";


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        library = new Library(mockDataBase, mockReview);
    }

    /**
     * Test to ensure that a valid book is successfully added to the library.
     * Validates if the addBook method correctly interacts with the database to add the book.
     */
    @Test
    void GivenValidBook_WhenAddBook_ThenSuccess() {
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(validAuthor);
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(null);

        library.addBook(mockBook);

        verify(mockDataBase).addBook(validBookISBN, mockBook); // Verify the interaction
    }

    /**
     * Test to ensure that adding a book with an invalid ISBN throws an IllegalArgumentException.
     */
    @Test
    void GivenInvalidISBN_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(mockBook.getISBN()).thenReturn(invalidBookISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(validAuthor);
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid ISBN.");
    }

    /**
     * Test to ensure that adding a book with a valid ISBN but an invalid title throws an IllegalArgumentException.
     */
    @Test
    void GivenValidISBNAndInvalidTitle_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.getTitle()).thenReturn(invalidBookTitle);
        when(mockBook.getAuthor()).thenReturn(validAuthor);
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid title.");
    }

    /**
     * Test to ensure that adding a book with a valid ISBN, title, but an invalid author (empty name) throws an IllegalArgumentException.
    */
    @Test
    void GivenValidISBNAndTitleAndInvalidAuthor_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(invalidAuthor);
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid author.");
    }

    /**
     * Test to ensure that adding a book that is already borrowed throws an IllegalArgumentException.
     */
    @Test
    void GivenValidISBNAndTitleAndAuthorButBookIsBorrowed_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(validAuthor);
        when(mockBook.isBorrowed()).thenReturn(true);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Book with invalid borrowed state.");
    }

    /**
     * Test to ensure that adding a book withInvalidISBN (as letters error) throws an IllegalArgumentException.
     * this way we test InvalidIsbn method (its private)
     */
    @Test
    void GivenBookWithInvalidISBNFormat_WhenAddBook_ThenThrowIllegalArgumentException() {
        String invalidISBN = "InvalidISBN";
        when(mockBook.getISBN()).thenReturn(invalidISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(validAuthor);
        when(mockBook.isBorrowed()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid ISBN.");
    }


    /**
     * Test to ensure that adding a book with Invalid Author Name (as symboles) throws an IllegalArgumentException.
     * this way we test validAuthor method (its private)
     */
    @Test
    void GivenBookWithInvalidAuthorName_WhenAddBook_ThenThrowIllegalArgumentException() {
        String invalidAuthorName = "!!Invalid@@Name##";
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(invalidAuthorName);
        when(mockBook.isBorrowed()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid author.");
    }

    /**
     * Test to ensure that adding a book with Invalid Author Name (as as consecutive special characters) throws an IllegalArgumentException.
     * validAuthor
     */
    @Test
    void GivenBookWithAuthorNameHavingConsecutiveSpecialCharacters_WhenAddBook_ThenThrowIllegalArgumentException() {
        String AuthorInvalidName = "Shlomi--Arbitman";
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        when(mockBook.getAuthor()).thenReturn(AuthorInvalidName);
        when(mockBook.isBorrowed()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid author.");
    }


    /**
     * Test to ensure that a valid user can register successfully.
     */
    @Test
    void GivenValidUser_WhenRegisterUser_ThenSuccess() {
        when(mockUser.getId()).thenReturn(validUserId);
        when(mockUser.getName()).thenReturn("Valid User");
        when(mockUser.getNotificationService()).thenReturn(mockNotification);
        when(mockDataBase.getUserById(validUserId)).thenReturn(null);

        library.registerUser(mockUser);

        verify(mockDataBase).registerUser(validUserId, mockUser); // Verify that the user is registered
    }

    /**
     * Test to ensure that registering a null user throws an IllegalArgumentException.
     */
    @Test
    void GivenNullUser_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.registerUser(null), "Invalid user.");
    }

    /**
     * Test to ensure that registering a user with an invalid ID throws an IllegalArgumentException.
     */
    @Test
    void GivenInvalidUserId_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        when(mockUser.getId()).thenReturn(invalidUserId);
        when(mockUser.getName()).thenReturn("Valid User");
        when(mockUser.getNotificationService()).thenReturn(mockNotification);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(mockUser), "Invalid user Id.");
    }

    /**
     * Test to ensure that registering a user with an invalid name throws an IllegalArgumentException.
     */
    @Test
    void GivenInvalidUserName_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        when(mockUser.getId()).thenReturn(validUserId);
        when(mockUser.getName()).thenReturn("");
        when(mockUser.getNotificationService()).thenReturn(mockNotification);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(mockUser), "Invalid user name.");
    }

    /**
     * Test to ensure that attempting to register an already existing user throws an IllegalArgumentException.
     */
    @Test
    void GivenExistingUser_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        when(mockUser.getId()).thenReturn(validUserId);
        when(mockUser.getName()).thenReturn("Valid User");
        when(mockUser.getNotificationService()).thenReturn(mockNotification);
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser); // User already exists

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(mockUser), "User already exists.");
    }

    /**
     * Test to ensure that registering a user with an invalid notification service throws an IllegalArgumentException.
     */
    @Test
    void GivenInvalidNotificationService_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        when(mockUser.getId()).thenReturn(validUserId);
        when(mockUser.getName()).thenReturn("Valid User");
        when(mockUser.getNotificationService()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.registerUser(mockUser), "Invalid notification service.");
    }






    @Test
    void GivenValidBookISBNAndValidUserId_WhenBorrowBook_ThenSuccess() {
        // Mocking the necessary objects and their behaviors
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(mockBook.getISBN())).thenReturn(mockBook);
        when(mockUser.getId()).thenReturn(validUserId);
        when(mockDataBase.getUserById(mockUser.getId())).thenReturn(mockUser);

        // Performing the action: borrowing a book
        library.borrowBook(mockBook.getISBN(), mockUser.getId());

        // Verifying that the borrowBook method is called with the correct parameters
        verify(mockDataBase).borrowBook(mockBook.getISBN(), mockUser.getId());
        // Verify that the book's borrow method is called
        verify(mockBook).borrow();
    }

    @Test
    void GivenInvalidBookISBNAndValidUserId_WhenBorrowBook_ThenIllegalArgumentException() {
        // Use an invalid ISBN, assuming it won't be found in the database
        when(mockBook.getISBN()).thenReturn(invalidBookISBN);
        when(mockUser.getId()).thenReturn(validUserId);
        when(mockDataBase.getBookByISBN(mockBook.getISBN())).thenReturn(null);  // Simulate the book not being found

        // Verify that the borrowBook method is not called
        verify(mockDataBase, never()).borrowBook(anyString(), anyString());  // Ensure the method is never called in this case

        // Verify that the book's borrow method is not called
        verify(mockBook, never()).borrow();

        // Perform the action and assert that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.borrowBook(mockBook.getISBN(), mockUser.getId()));

        // Verify that the exception message is correct
        assertEquals("Invalid ISBN.", exception.getMessage());
    }

    @Test
    void GivenValidBookISBNAndInvalidUserId_WhenBorrowBook_ThenIllegalArgumentException() {
        // Use a valid ISBN, but assume the user ID is invalid
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        when(mockUser.getId()).thenReturn(invalidUserId);
        when(mockDataBase.getBookByISBN(mockBook.getISBN())).thenReturn(mockBook);  // Simulate the book being found

        // Perform the action and assert that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.borrowBook(mockBook.getISBN(), mockUser.getId()));

        // Verify that the exception message is correct
        assertEquals("Invalid user Id.", exception.getMessage());

        // Verify that the borrowBook method is not called
        verify(mockDataBase, never()).borrowBook(anyString(), anyString());  // Ensure the method is never called in this case
    }

    @Test
    void GivenValidBorrowedBookISBN_WhenReturnBook_ThenSuccess() {
        // Use a valid ISBN assuming the book is currently borrowed
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        // Simulate the book being found
        when(mockDataBase.getBookByISBN(mockBook.getISBN())).thenReturn(mockBook);
        // Simulate the book being currently borrowed
        when(mockBook.isBorrowed()).thenReturn(true);
        // Perform the action: returning a borrowed book
        library.returnBook(mockBook.getISBN());
        // Verify that the book's returnBook method is called
        verify(mockBook).returnBook();
        // Verify that the databaseService's returnBook method is called
        verify(mockDataBase).returnBook(mockBook.getISBN());
    }

    @Test
    void GivenValidNonBorrowedBookISBN_WhenReturnBook_ThenBookNotBorrowedException() {
        // Use a valid ISBN assuming the book is not currently borrowed
        when(mockBook.getISBN()).thenReturn(validBookISBN);
        // Simulate the book being found
        when(mockDataBase.getBookByISBN(mockBook.getISBN())).thenReturn(mockBook);
        // Simulate the book not being currently borrowed
        when(mockBook.isBorrowed()).thenReturn(false);
        // Perform the action and assert that a BookNotBorrowedException is thrown
        BookNotBorrowedException exception = assertThrows(BookNotBorrowedException.class, () -> library.returnBook(mockBook.getISBN()));
        // Verify that the exception message is correct
        assertEquals("Book wasn't borrowed!", exception.getMessage());
        // Verify that the book's returnBook method is not called
        verify(mockBook, never()).returnBook();
        // Verify that the databaseService's returnBook method is not called
        verify(mockDataBase, never()).returnBook(anyString());
    }

    @Test
    void GivenInvalidBookISBN_WhenReturnBook_ThenIllegalArgumentException() {
        // Use an invalid ISBN
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);  // Simulate the book not being found
        // Perform the action and assert that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.returnBook(invalidBookISBN));
        // Verify that the exception message is correct
        assertEquals("Invalid ISBN.", exception.getMessage());
        // Verify that the book's returnBook method is not called
        verify(mockBook, never()).returnBook();
        // Verify that the databaseService's returnBook method is not called
        verify(mockDataBase, never()).returnBook(anyString());
    }
    @Test
    void GivenValidISBNAndValidUserIdWithReviews_WhenNotifyUserWithBookReviews_ThenSuccess() {
        // Stubbing the mock book to return validBookTitle
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Stubbing the mock database to return a user for the given user ID
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser);
        // Stubbing the review service to return reviews for the specified book
        List<String> reviews = Arrays.asList("Great book!", "Must-read!");
        when(mockReview.getReviewsForBook(validBookISBN)).thenReturn(reviews);
        // Perform the action: notifying the user with book reviews
        library.notifyUserWithBookReviews(validBookISBN, validUserId);
        // Verify that the review service's getReviewsForBook method is called with the correct parameters
        verify(mockReview).getReviewsForBook(validBookISBN);
        // Verify that the user's sendNotification method is called with the correct message
        verify(mockUser).sendNotification(String.format("Reviews for '%s':\nGreat book!\nMust-read!", validBookTitle));
    }
    @Test
    void GivenValidISBNAndValidUserIdWithoutReviews_WhenNotifyUserWithBookReviews_ThenNoReviewsFoundException() {
        // Stubbing the mock book to return validBookTitle
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Stubbing the mock database to return a user for the given user ID
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser);
        // Stubbing the review service to return no reviews for the specified book
        when(mockReview.getReviewsForBook(validBookISBN)).thenReturn(Collections.emptyList());
        // Perform the action and assert that a NoReviewsFoundException is thrown
        NoReviewsFoundException exception = assertThrows(NoReviewsFoundException.class, () -> library.notifyUserWithBookReviews(validBookISBN, validUserId));
        // Verify that the exception message is correct
        assertEquals("No reviews found!", exception.getMessage());
        // Verify that the review service's getReviewsForBook method is called with the correct parameters
        verify(mockReview).getReviewsForBook(validBookISBN);
        // Verify that the user's sendNotification method is not called (no reviews found)
        verify(mockUser, never()).sendNotification(anyString());
    }
    @Test
    void GivenInvalidISBNAndValidUserId_WhenNotifyUserWithBookReviews_ThenInvalidISBNException() {
        // Use an invalid ISBN, assuming it won't be found in the database
        when(mockDataBase.getBookByISBN(invalidBookISBN)).thenReturn(null);
        // Stubbing the mock database to return a user for the given user ID
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser);
        // Perform the action and assert that an InvalidISBNException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.notifyUserWithBookReviews(invalidBookISBN, validUserId));
        // Verify that the exception message is correct
        assertEquals("Invalid ISBN.", exception.getMessage());
        // Verify that the review service's getReviewsForBook method is not called
        verify(mockReview, never()).getReviewsForBook(anyString());
        // Verify that the user's sendNotification method is not called (invalid ISBN)
        verify(mockUser, never()).sendNotification(anyString());
    }
    @Test
    void GivenValidISBNAndInvalidUserId_WhenNotifyUserWithBookReviews_ThenInvalidUserIdException() {
        // Stubbing the mock book to return validBookTitle
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Use an invalid user ID, assuming it won't be found in the database
        when(mockDataBase.getUserById(invalidUserId)).thenReturn(null);
        // Perform the action and assert that an InvalidUserIdException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.notifyUserWithBookReviews(validBookISBN, invalidUserId));
        // Verify that the exception message is correct
        assertEquals("Invalid user Id.", exception.getMessage());
        // Verify that the review service's getReviewsForBook method is not called
        verify(mockReview, never()).getReviewsForBook(anyString());
        // Verify that the user's sendNotification method is not called (invalid user ID)
        verify(mockUser, never()).sendNotification(anyString());
    }
    @Test
    void GivenValidISBNAndValidUserIdWithReviewServiceUnavailable_WhenNotifyUserWithBookReviews_ThenReviewServiceUnavailableException() {
        // Stubbing the mock book to return validBookTitle
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Stubbing the mock database to return a user for the given user ID
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser);
        // Stubbing the review service to throw a ReviewServiceUnavailableException
        when(mockReview.getReviewsForBook(validBookISBN)).thenThrow(new ReviewServiceUnavailableException("Service unavailable"));
        // Perform the action and assert that a ReviewServiceUnavailableException is thrown
        ReviewServiceUnavailableException exception = assertThrows(ReviewServiceUnavailableException.class, () -> library.notifyUserWithBookReviews(validBookISBN, validUserId));
        // Verify that the exception message is correct
        assertEquals("Service unavailable", exception.getMessage());
        // Verify that the user's sendNotification method is not called (review service unavailable)
        verify(mockUser, never()).sendNotification(anyString());
    }
    @Test
    void G1ivenValidISBNAndValidUserIdWithNotificationException_WhenNotifyUserWithBookReviews_ThenNotificationException() {
        // Stubbing the mock book to return validBookTitle
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Stubbing the mock database to return a user for the given user ID
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser);
        // Stubbing the review service to return reviews for the specified book
        List<String> reviews = Arrays.asList("Great book!", "Must-read!");
        when(mockReview.getReviewsForBook(validBookISBN)).thenReturn(reviews);
        // Stubbing the user's sendNotification method to throw a NotificationException
        doThrow(new NotificationException("Notification failed!")).when(mockUser).sendNotification(anyString());
        // Perform the action and assert that a NotificationException is thrown
        NotificationException exception = assertThrows(NotificationException.class, () -> library.notifyUserWithBookReviews(validBookISBN, validUserId));
        // Verify that the exception message is correct
        assertEquals("Notification failed!", exception.getMessage());
        // Verify that the review service's getReviewsForBook method is called with the correct parameters
        verify(mockReview).getReviewsForBook(validBookISBN);
        // Verify that the user's sendNotification method is retried up to 5 times
        verify(mockUser, times(5)).sendNotification(anyString());
    }

    @Test
    void GivenValidISBNAndUserNotFound_WhenNotifyUserWithBookReviews_ThenUserNotRegisteredException() {
        // Stub the mock database to return null when getUserById is called
        when(mockDataBase.getUserById(validUserId)).thenReturn(null);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Act and Assert
        UserNotRegisteredException exception = assertThrows(UserNotRegisteredException.class, () -> library.notifyUserWithBookReviews(validBookISBN, validUserId));
        // Verify that the exception message is correct
        assertEquals("User not found!", exception.getMessage());
        // Verify that the getUserById method is called with the correct parameter
        verify(mockDataBase).getUserById(validUserId);
        // Verify that other methods are not called (optional, depending on your design)
        verifyNoMoreInteractions(mockReview, mockUser);
    }

    @Test
    void GivenValidISBNAndValidUserId_WhenGetBookByISBN_ThenReturnBook() {
        when(mockBook.isBorrowed()).thenReturn(false);
        // Stubbing the mock book to return validBookTitle
        when(mockBook.getTitle()).thenReturn(validBookTitle);
        // Stubbing the mock database to return a book for the given ISBN
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Stubbing the mock database to return a user for the given user ID
        when(mockDataBase.getUserById(validUserId)).thenReturn(mockUser);
        // Stubbing the review service to return reviews for the specified book
        List<String> reviews = Arrays.asList("Great book!", "Must-read!");
        when(mockReview.getReviewsForBook(validBookISBN)).thenReturn(reviews);

        // Performing the action: retrieving a book by ISBN
        Book result = library.getBookByISBN(validBookISBN, validUserId);

        // Verifying that the expected interactions occurred
        verify(mockDataBase, times(2)).getBookByISBN(validBookISBN);
        verify(mockBook).isBorrowed();

        // Asserting the result
        assertNotNull(result);
        assertEquals(mockBook, result);
    }

    @Test
    void GivenInvalidISBN_WhenGetBookByISBN_ThenThrowIllegalArgumentException() {
        // Performing the action and asserting that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(invalidBookISBN, invalidUserId));
        // Verifying that the exception message is correct
        assertEquals("Invalid ISBN.", exception.getMessage());
        // Verifying that no interactions occurred with the database service
        verifyNoInteractions(mockDataBase);
    }

    @Test
    void GivenInvalidUserIdFormat_WhenGetBookByISBN_ThenThrowIllegalArgumentException() {
        // Performing the action and asserting that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(validBookISBN, invalidUserId));
        // Verifying that the exception message is correct
        assertEquals("Invalid user Id.", exception.getMessage());
        // Verifying that no interactions occurred with the database service
        verifyNoInteractions(mockDataBase);
    }

    @Test
    void GivenBookNotFoundForISBN_WhenGetBookByISBN_ThenThrowBookNotFoundException() {
        // Mocking the necessary objects and their behaviors
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(null);
        // Performing the action and asserting that a BookNotFoundException is thrown
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> library.getBookByISBN(validBookISBN, validUserId));
        // Verifying that the exception message is correct
        assertEquals("Book not found!", exception.getMessage());
        // Verifying the interaction with the database service
        verify(mockDataBase).getBookByISBN(validBookISBN);
        verifyNoMoreInteractions(mockDataBase);
    }

    @Test
    void GivenBorrowedBookForISBN_WhenGetBookByISBN_ThenThrowBookAlreadyBorrowedException() {
        // Mocking the necessary objects and their behaviors
        when(mockBook.isBorrowed()).thenReturn(true);
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Performing the action and asserting that a BookAlreadyBorrowedException is thrown
        BookAlreadyBorrowedException exception = assertThrows(BookAlreadyBorrowedException.class, () -> library.getBookByISBN(validBookISBN, validUserId));
        // Verifying that the exception message is correct
        assertEquals("Book was already borrowed!", exception.getMessage());
        // Verifying the interactions with the database service and the book
        verify(mockDataBase).getBookByISBN(validBookISBN);
        verify(mockBook).isBorrowed();
        verifyNoMoreInteractions(mockDataBase, mockBook);
    }

    @Test
    void GivenNotificationFailure_WhenGetBookByISBN_ThenContinueAndReturnBook() {
        // Creating a spy of the library instance
        Library librarySpy = spy(library);
        // Mocking the necessary objects and their behaviors
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(validBookISBN)).thenReturn(mockBook);
        // Stubbing the notifyUserWithBookReviews to throw an exception
        doThrow(new RuntimeException("Notification failed!")).when(librarySpy).notifyUserWithBookReviews(validBookISBN, validUserId);
        // Performing the action: retrieving a book by ISBN
        Book result = librarySpy.getBookByISBN(validBookISBN, validUserId);
        // Verifying that the expected interactions occurred
        verify(mockDataBase, times(1)).getBookByISBN(validBookISBN);
        verify(mockBook).isBorrowed();
        verify(librarySpy, times(1)).notifyUserWithBookReviews(validBookISBN, validUserId);
        // Asserting the result
        assertNotNull(result);
        assertEquals(mockBook, result);
    }


}
