import java.util.Comparator;

public class CategoryComparator implements Comparator<Tome> {
    @Override
    public int compare(Tome a, Tome b) {
        return a.getCategory().compareToIgnoreCase(b.getCategory());
    }
}
