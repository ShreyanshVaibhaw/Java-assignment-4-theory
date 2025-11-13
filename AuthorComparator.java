import java.util.Comparator;

public class AuthorComparator implements Comparator<Tome> {
    @Override
    public int compare(Tome a, Tome b) {
        return a.getAuthor().compareToIgnoreCase(b.getAuthor());
    }
}

