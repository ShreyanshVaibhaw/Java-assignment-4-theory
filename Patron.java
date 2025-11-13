import java.util.ArrayList;
import java.util.List;

public class Patron {
    private Integer memberId;
    private String name;
    private String email;
    private List<Integer> issuedBooks;

    public Patron(Integer memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name == null ? "" : name.trim();
        this.email = email == null ? "" : email.trim();
        this.issuedBooks = new ArrayList<>();
    }

    public Integer getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Integer> getIssuedBooks() { return new ArrayList<>(issuedBooks); }

    public void addIssuedBook(int bookId) { issuedBooks.add(bookId); }
    public boolean returnIssuedBook(int bookId) { return issuedBooks.remove(Integer.valueOf(bookId)); }

    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(memberId).append(",").append(escape(name)).append(",").append(escape(email)).append(",");
        if (!issuedBooks.isEmpty()) {
            for (int i = 0; i < issuedBooks.size(); i++) {
                if (i > 0) sb.append(";");
                sb.append(issuedBooks.get(i));
            }
        }
        return sb.toString();
    }

    private String escape(String s) { return s.replace(",", " "); }

    @Override
    public String toString() {
        return String.format("MemberID:%d | Name:%s | Email:%s | Issued:%s",
                memberId, name, email, issuedBooks.toString());
    }
}
