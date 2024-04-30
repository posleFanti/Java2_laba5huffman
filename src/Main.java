import com.sun.source.tree.Tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

class SortByValue implements Comparator<Entry<Character, Integer>> {
    @Override
    public int compare(Entry<Character, Integer> o1, Entry<Character, Integer> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }
}

public class Main {
    private static Map<Character, Integer> sortByComparator(Map<Character, Integer> map) {
        List<Entry<Character, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort(new SortByValue().reversed());
        Map<Character, Integer> sortedMap = new LinkedHashMap<>();
        for (Entry<Character, Integer> e : list) {
            sortedMap.put(e.getKey(), e.getValue());
        }
        return sortedMap;
    }

    public static void main(String[] args) {
        ArrayList<String> fileLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("lorem.txt"))) {
            String str;
            while ((str = br.readLine()) != null)
                fileLines.add(str);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        for (String s : fileLines) {
            System.out.println(s);
        }
        Map<Character, Integer> alphabet = new HashMap<>();
        for (String s : fileLines) {
            char[] chars = s.toCharArray();
            for (char aChar : chars) {
                if (alphabet.containsKey(aChar)) {
                    int value = alphabet.get(aChar);
                    value++;
                    alphabet.put(aChar, value);
                } else {
                    alphabet.put(aChar, 1);
                }
            }
        }
        Map<Character, Integer> sortedAlphabet = sortByComparator(alphabet);
    }
}