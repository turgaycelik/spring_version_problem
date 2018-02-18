package hello;

public interface BookRepository {

    Book getByIsbn(String isbn);

    public void deleteBook(String isbn);

}
