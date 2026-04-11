import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Problem 4: Plagiarism Detection System
 *
 * Uses HashMap to store n-grams with document references,
 * computes similarity percentages between documents.
 */
public class Problem4 {

    /** Maps n-gram -> set of document IDs containing it. */
    private HashMap<String, Set<String>> ngramIndex;

    /** Maps documentId -> list of its n-grams. */
    private HashMap<String, List<String>> documentNgrams;

    /** N-gram size. */
    private int ngramSize;

    public Problem4(int ngramSize) {
        this.ngramSize = ngramSize;
        this.ngramIndex = new HashMap<>();
        this.documentNgrams = new HashMap<>();
    }

    /**
     * Extracts n-grams from text.
     *
     * @param text the input text
     * @return list of n-grams
     */
    private List<String> extractNgrams(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - ngramSize; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ngramSize; j++) {
                if (j > 0) sb.append(" ");
                sb.append(words[i + j]);
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    /**
     * Adds a document to the index.
     *
     * @param documentId document identifier
     * @param content document text content
     * @return number of n-grams extracted
     */
    public int addDocument(String documentId, String content) {
        List<String> ngrams = extractNgrams(content);
        documentNgrams.put(documentId, ngrams);

        for (String ngram : ngrams) {
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(documentId);
        }
        return ngrams.size();
    }

    /**
     * Analyzes a document against all indexed documents
     * and returns similarity results.
     *
     * @param documentId the document to analyze
     * @return list of similarity results
     */
    public List<String> analyzeDocument(String documentId) {
        List<String> results = new ArrayList<>();
        List<String> ngrams = documentNgrams.get(documentId);

        if (ngrams == null) {
            results.add("Document not found: " + documentId);
            return results;
        }

        results.add("Extracted " + ngrams.size() + " n-grams");

        // Count matching n-grams per other document
        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String ngram : ngrams) {
            Set<String> docs = ngramIndex.get(ngram);
            if (docs != null) {
                for (String doc : docs) {
                    if (!doc.equals(documentId)) {
                        matchCounts.put(doc, matchCounts.getOrDefault(doc, 0) + 1);
                    }
                }
            }
        }

        // Calculate similarity for each matching document
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String otherDoc = entry.getKey();
            int matches = entry.getValue();
            double similarity = (matches * 100.0) / ngrams.size();

            String status;
            if (similarity > 50) {
                status = "PLAGIARISM DETECTED";
            } else if (similarity > 10) {
                status = "suspicious";
            } else {
                status = "likely original";
            }

            results.add(String.format("Found %d matching n-grams with \"%s\" -> Similarity: %.1f%% (%s)",
                    matches, otherDoc, similarity, status));
        }

        if (matchCounts.isEmpty()) {
            results.add("No matches found. Document appears original.");
        }

        return results;
    }

    /**
     * Compares two documents using linear search (for benchmarking).
     *
     * @param content1 first document text
     * @param content2 second document text
     * @return number of matching n-grams found
     */
    public int linearSearchCompare(String content1, String content2) {
        List<String> ngrams1 = extractNgrams(content1);
        List<String> ngrams2 = extractNgrams(content2);
        int matches = 0;

        for (String ng1 : ngrams1) {
            for (String ng2 : ngrams2) {
                if (ng1.equals(ng2)) {
                    matches++;
                    break;
                }
            }
        }
        return matches;
    }

    public static void main(String[] args) {
        Problem4 detector = new Problem4(5); // 5-grams

        // Add documents
        String essay89 = "The quick brown fox jumps over the lazy dog and then the fox runs across the field "
                + "to find food for the winter season ahead while the dog sleeps peacefully under the tree";

        String essay92 = "The quick brown fox jumps over the lazy dog and then the fox runs across the field "
                + "to find food for the winter season ahead while the dog sleeps peacefully under the tree "
                + "and the birds sing in the morning light as the sun rises over the mountains";

        String essay123 = "Machine learning algorithms can process large datasets efficiently using neural networks "
                + "and deep learning techniques that enable pattern recognition in complex data structures "
                + "while optimizing performance through gradient descent and backpropagation methods";

        int n1 = detector.addDocument("essay_089.txt", essay89);
        int n2 = detector.addDocument("essay_092.txt", essay92);
        int n3 = detector.addDocument("essay_123.txt", essay123);

        System.out.println("Indexed essay_089.txt: " + n1 + " n-grams");
        System.out.println("Indexed essay_092.txt: " + n2 + " n-grams");
        System.out.println("Indexed essay_123.txt: " + n3 + " n-grams");

        // Analyze essay_123
        System.out.println("\nanalyzeDocument(\"essay_123.txt\")");
        for (String result : detector.analyzeDocument("essay_123.txt")) {
            System.out.println("  -> " + result);
        }

        // Analyze essay_092
        System.out.println("\nanalyzeDocument(\"essay_092.txt\")");
        for (String result : detector.analyzeDocument("essay_092.txt")) {
            System.out.println("  -> " + result);
        }

        // Performance comparison
        System.out.println("\n--- Performance Benchmark ---");
        long start = System.nanoTime();
        detector.analyzeDocument("essay_092.txt");
        long hashTime = System.nanoTime() - start;

        start = System.nanoTime();
        detector.linearSearchCompare(essay92, essay89);
        long linearTime = System.nanoTime() - start;

        System.out.println("Hash-based analysis: " + hashTime / 1000 + " microseconds");
        System.out.println("Linear search comparison: " + linearTime / 1000 + " microseconds");
    }
}
