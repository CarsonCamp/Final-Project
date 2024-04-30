import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SeparateChainingHashTable {
    private LinkedList<String>[] hashTable;
    private int tableSize;

    @SuppressWarnings("unchecked")
    public SeparateChainingHashTable(int size) {
        this.tableSize = size;
        hashTable = (LinkedList<String>[]) new LinkedList[size];
        for (int i = 0; i < size; i++) {
            hashTable[i] = new LinkedList<>();
        }
    }

    private int hash(String word) {
        return (word.hashCode() & 0x7fffffff) % tableSize;
    }

    public void insert(String word) {
        int hashValue = hash(word);
        hashTable[hashValue].add(word);
    }

    public int search(String word) {
        int hashValue = hash(word);
        List<String> chain = hashTable[hashValue];
        int probeCount = 0;

        for (String element : chain) {
            probeCount++;
            if (element.equals(word)) {
                return probeCount;
            }
        }
        return -1;
    }

    public void performSingleSearch(List<String> words) {
        if (words.isEmpty()) return;

        Random rand = new Random();
        String searchWord = words.get(rand.nextInt(words.size()));

        long startTime = System.nanoTime(); // start time
        int probes = search(searchWord); // do search and get probe count
        long endTime = System.nanoTime(); // end time

        long searchTime = endTime - startTime;
        double searchTimeMs = (double)searchTime / 1_000_000; // convert to milliseconds

        System.out.printf("Word: '%s'\nTime Taken: %.3f ms\nProbes Used: %d%n", searchWord, searchTimeMs, probes);
    }

    public void performMultipleSearches(List<String> words, int numberOfSearches) {
        Random rand = new Random();
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        long totalTime = 0;
        int totalProbes = 0;

        for (int i = 0; i < numberOfSearches; i++) {
            String searchWord = words.get(rand.nextInt(words.size()));
            long startTime = System.nanoTime();
            int probes = search(searchWord);
            long endTime = System.nanoTime();
            long searchTime = endTime - startTime;

            minTime = Math.min(minTime, searchTime);
            maxTime = Math.max(maxTime, searchTime);
            totalTime += searchTime;
            totalProbes += probes;
        }

        double avgTime = (double)totalTime / numberOfSearches;
        System.out.printf("For %d searches: Min Time: %.3f ms, Avg Time: %.3f ms, Max Time: %.3f ms, Total Probes: %d%n",
                          numberOfSearches, (double)minTime / 1_000_000, avgTime / 1_000_000, (double)maxTime / 1_000_000, totalProbes);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter table size: ");
        int tableSize = scanner.nextInt();

        SeparateChainingHashTable scht = new SeparateChainingHashTable(tableSize);
        String filePath = "/Users/carsoncampbell/Documents/vscode/SeperateChaining/bin/words.txt";

        try {
            List<String> words = Files.readAllLines(Paths.get(filePath));
            words.forEach(scht::insert);

            // do a single search
            scht.performSingleSearch(words);

            // do multiple searches
            int[] searchSets = {10, 20, 30, 40, 50};
            for (int numSearches : searchSets) {
                scht.performMultipleSearches(words, numSearches);
            }

        } catch (IOException e) {
            System.out.println("Error reading the words file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
