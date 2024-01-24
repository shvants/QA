package ac.il.bgu.qa;

import ac.il.bgu.qa.errors.*;
import ac.il.bgu.qa.services.*;;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        library = new Library(mockDataBase,mockReview);
    }


    @Test
    void GivenBook_WhenAddBook_ThenSuccess() {

        when(mockBook.getISBN()).thenReturn("9780306406157");
        when(mockBook.getTitle()).thenReturn("VV");
        when(mockBook.getAuthor()).thenReturn("V");
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN("9780306406157")).thenReturn(null);
        library.addBook(mockBook);
        verify(mockDataBase).addBook("9780306406157", mockBook); //Integration
    }

    @Test
    void AddInvalidBookISBNUnsuccessfully() {

        when(mockBook.getISBN()).thenReturn("1234567891234");
        when(mockBook.getTitle()).thenReturn("VV");
        when(mockBook.getAuthor()).thenReturn("V");
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid ISBN.");
    }

    @Test
    void AddValidBookISBNInvalidBookTitleUnsuccessfully() {

        when(mockBook.getISBN()).thenReturn("9780306406157");
        when(mockBook.getTitle()).thenReturn("");
        when(mockBook.getAuthor()).thenReturn("V");
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid title.");
    }

    @Test
    void AddValidBookISBNvalidBookTitleInvalidBookAuthorUnsuccessfully() {

        when(mockBook.getISBN()).thenReturn("9780306406157");
        when(mockBook.getTitle()).thenReturn("V");
        when(mockBook.getAuthor()).thenReturn("");
        when(mockBook.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Invalid author.");
    }
    @Test
    void AddValidBookISBNValidBookTitleValidBookAuthorInvalidBorrowedUnsuccessfully() {

        when(mockBook.getISBN()).thenReturn("9780306406157");
        when(mockBook.getTitle()).thenReturn("V");
        when(mockBook.getAuthor()).thenReturn("VV");
        when(mockBook.isBorrowed()).thenReturn(true);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(mockBook), "Book with invalid borrowed state.");
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            library.returnBook(invalidBookISBN);
        });
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
        NotificationException exception = assertThrows(NotificationException.class, () -> {
            library.notifyUserWithBookReviews(validBookISBN, validUserId);
        });
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

}
