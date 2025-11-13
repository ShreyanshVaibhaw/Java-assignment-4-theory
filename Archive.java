import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Archive {
    private Map<Integer, Tome> tomes = new HashMap<>();
    private Map<Integer, Patron> patrons = new HashMap<>();
    private Set<String> categories = new HashSet<>();
    private final String tomesFile;
    private final String patronsFile;
    private int nextBookId = 100;
    private int nextMemberId = 200;

    private static final Pattern EMAIL_RE = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public Archive(String tomesFile, String patronsFile) {
        this.tomesFile = tomesFile == null ? "catalogue.txt" : tomesFile;
        this.patronsFile = patronsFile == null ? "registry.txt" : patronsFile;
        loadFromFiles();
    }

    private void loadFromFiles() {
        File f1 = new File(tomesFile);
        File f2 = new File(patronsFile);
        if (!f1.exists()) createEmptyFile(f1, "bookId,title,author,category,issued");
        if (!f2.exists()) createEmptyFile(f2, "memberId,name,email,issuedBooks");
        loadTomes();
        loadPatrons();
        computeNextIds();
    }

    private void createEmptyFile(File f, String header) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(header);
            bw.newLine();
        } catch (IOException e) { /* ignore */ }
    }

    private void loadTomes() {
        try (BufferedReader br = new BufferedReader(new FileReader(tomesFile))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 5) continue;
                Integer id = Integer.valueOf(p[0].trim());
                String title = p[1].trim();
                String author = p[2].trim();
                String cat = p[3].trim();
                boolean issued = Boolean.parseBoolean(p[4].trim());
                Tome t = new Tome(id, title, author, cat, issued);
                tomes.put(id, t);
                if (!cat.isEmpty()) categories.add(cat);
            }
        } catch (Exception e) { System.out.println("Error loading books: " + e.getMessage()); }
    }

    private void loadPatrons() {
        try (BufferedReader br = new BufferedReader(new FileReader(patronsFile))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 3) continue;
                Integer id = Integer.valueOf(p[0].trim());
                String name = p[1].trim();
                String email = p[2].trim();
                Patron patron = new Patron(id, name, email);
                if (p.length >= 4 && !p[3].trim().isEmpty()) {
                    String[] issued = p[3].split(";", -1);
                    for (String s : issued) {
                        try {
                            int bid = Integer.parseInt(s.trim());
                            patron.addIssuedBook(bid);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                patrons.put(id, patron);
            }
        } catch (Exception e) { System.out.println("Error loading members: " + e.getMessage()); }
    }

    private void computeNextIds() {
        if (!tomes.isEmpty()) nextBookId = Collections.max(tomes.keySet()) + 1;
        if (!patrons.isEmpty()) nextMemberId = Collections.max(patrons.keySet()) + 1;
    }

    public int addBook(String title, String author, String category) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title required.");
        if (author == null || author.trim().isEmpty()) throw new IllegalArgumentException("Author required.");
        category = category == null ? "" : category.trim();
        int id = nextBookId++;
        Tome t = new Tome(id, title, author, category, false);
        tomes.put(id, t);
        if (!category.isEmpty()) categories.add(category);
        saveTomes();
        return id;
    }

    public int addMember(String name, String email) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name required.");
        if (email == null || !EMAIL_RE.matcher(email.trim()).matches()) throw new IllegalArgumentException("Invalid email.");
        int id = nextMemberId++;
        Patron p = new Patron(id, name, email);
        patrons.put(id, p);
        savePatrons();
        return id;
    }

    public boolean issueBook(int bookId, int memberId) {
        Tome t = tomes.get(bookId);
        Patron p = patrons.get(memberId);
        if (t == null) throw new IllegalArgumentException("Book not found.");
        if (p == null) throw new IllegalArgumentException("Member not found.");
        if (t.isIssued()) return false;
        t.markAsIssued();
        p.addIssuedBook(bookId);
        saveTomes(); savePatrons();
        return true;
    }

    public boolean returnBook(int bookId, int memberId) {
        Tome t = tomes.get(bookId);
        Patron p = patrons.get(memberId);
        if (t == null) throw new IllegalArgumentException("Book not found.");
        if (p == null) throw new IllegalArgumentException("Member not found.");
        boolean removed = p.returnIssuedBook(bookId);
        if (!removed) return false;
        t.markAsReturned();
        saveTomes(); savePatrons();
        return true;
    }

    public List<Tome> searchBooksByTitle(String fragment) {
        String f = fragment == null ? "" : fragment.toLowerCase();
        return tomes.values().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public List<Tome> searchBooksByAuthor(String fragment) {
        String f = fragment == null ? "" : fragment.toLowerCase();
        return tomes.values().stream()
                .filter(t -> t.getAuthor().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public List<Tome> searchBooksByCategory(String category) {
        String f = category == null ? "" : category.toLowerCase();
        return tomes.values().stream()
                .filter(t -> t.getCategory().toLowerCase().contains(f))
                .collect(Collectors.toList());
    }

    public List<Tome> sortBooksByTitle() {
        List<Tome> list = new ArrayList<>(tomes.values());
        Collections.sort(list); // Comparable
        return list;
    }

    public List<Tome> sortBooksByAuthor() {
        List<Tome> list = new ArrayList<>(tomes.values());
        list.sort(new AuthorComparator());
        return list;
    }

    public List<Tome> sortBooksByCategory() {
        List<Tome> list = new ArrayList<>(tomes.values());
        list.sort(new CategoryComparator());
        return list;
    }

    public Set<String> getCategories() { return new TreeSet<>(categories); }

    private void saveTomes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tomesFile))) {
            bw.write("bookId,title,author,category,issued"); bw.newLine();
            for (Tome t : tomes.values()) {
                bw.write(t.toCSV()); bw.newLine();
            }
        } catch (IOException e) { System.out.println("Save books error: " + e.getMessage()); }
    }

    private void savePatrons() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(patronsFile))) {
            bw.write("memberId,name,email,issuedBooks"); bw.newLine();
            for (Patron p : patrons.values()) {
                bw.write(p.toCSV()); bw.newLine();
            }
        } catch (IOException e) { System.out.println("Save members error: " + e.getMessage()); }
    }

    public void saveAll() { saveTomes(); savePatrons(); }
}
