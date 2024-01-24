package ac.il.bgu.qa;

import ac.il.bgu.qa.errors.*;
import ac.il.bgu.qa.services.*;;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestLibrary {

    @Mock
    DatabaseService mockDataBase;
    @Mock
    NotificationService mockNotification;
    @Mock
    ReviewService mockreview;

    @Mock
    Book book;

    @Mock
    User user;

    Library library;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        library = new Library(mockDataBase,mockreview);
    }


    @Test
    void GivenBook_WhenaddBook_ThenSuccess() {

        when(book.getISBN()).thenReturn("9780306406157");
        when(book.getTitle()).thenReturn("VV");
        when(book.getAuthor()).thenReturn("V");
        when(book.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN("9780306406157")).thenReturn(null);
        library.addBook(book);
        verify(mockDataBase).addBook("9780306406157",book); //Integration
    }

    @Test
    void AddInvalidBookISBNUnsuccessfully() {

        when(book.getISBN()).thenReturn("1234567891234");
        when(book.getTitle()).thenReturn("VV");
        when(book.getAuthor()).thenReturn("V");
        when(book.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(book), "Invalid ISBN.");
    }

    @Test
    void AddValidBookISBNInvalidBookTitleUnsuccessfully() {

        when(book.getISBN()).thenReturn("9780306406157");
        when(book.getTitle()).thenReturn("");
        when(book.getAuthor()).thenReturn("V");
        when(book.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(book), "Invalid title.");
    }

    @Test
    void AddValidBookISBNvalidBookTitleInvalidBookAuthorUnsuccessfully() {

        when(book.getISBN()).thenReturn("9780306406157");
        when(book.getTitle()).thenReturn("V");
        when(book.getAuthor()).thenReturn("");
        when(book.isBorrowed()).thenReturn(false);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(book), "Invalid author.");
    }
    @Test
    void AddValidBookISBNvalidBookTitlevalidBookAuthorInvalidBorrowedUnsuccessfully() {

        when(book.getISBN()).thenReturn("9780306406157");
        when(book.getTitle()).thenReturn("V");
        when(book.getAuthor()).thenReturn("VV");
        when(book.isBorrowed()).thenReturn(true);
        when(mockDataBase.getBookByISBN(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> library.addBook(book), "Book with invalid borrowed state.");
    }
}
