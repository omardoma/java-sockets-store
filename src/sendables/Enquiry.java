package sendables;

import java.io.Serializable;

public class Enquiry implements Serializable {
    private String bookId;
    private String bookName;

    public Enquiry(String bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }
}
