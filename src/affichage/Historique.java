package affichage;

import java.util.LinkedList;
import java.util.List;

public class Historique {
    private static final int MAX_HISTORY_SIZE = 10;
    private LinkedList<String> history;

    public Historique() {
        history = new LinkedList<>();
    }

    public void addToHistory(String imagePath) {
        if (history.contains(imagePath)) {
            history.remove(imagePath);
        }
        history.addFirst(imagePath);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeLast();
        }
    }

    public void renameHistoryEntry(String oldEntry, String newEntry) {
        int index = history.indexOf(oldEntry);
        if (index != -1) {
            history.set(index, newEntry);
        }
    }

    public List<String> getHistory() {
        return history;
    }
}
