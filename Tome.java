import java.util.Objects;

public class Tome implements Comparable<Tome> {
    private Integer bookId;
    private String title;
    private String author;
    private String category;
    private boolean issued;

    public Tome(Integer bookId, String title, String author, String category, boolean issued) {
        this.bookId = bookId;
        this.title = title == null ? "" : title.trim();
        this.author = author == null ? "" : author.trim();
        this.category = category == null ? "" : category.trim();
        this.issued = issued;
    }

    public Integer getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isIssued() { return issued; }

    public void markAsIssued() { this.issued = true; }
    public void markAsReturned() { this.issued = false; }

    public String toCSV() {
        return bookId + "," + escape(title) + "," + escape(author) + "," + escape(category) + "," + issued;
    }

    private String escape(String s) {
        return s.replace(",", " ");
    }

    @Override
    public String toString() {
        return String.format("ID:%d | Title:%s | Author:%s | Cat:%s | Issued:%s",
                bookId, title, author, category, issued ? "YES" : "NO");
    }

    @Override
    public int compareTo(Tome o) {
        return this.title.compareToIgnoreCase(o.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tome)) return false;
        Tome tome = (Tome) o;
        return Objects.equals(bookId, tome.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }
}
