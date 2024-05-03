import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

class SortByValue implements Comparator<Entry<Character, Integer>> {
    @Override
    public int compare(Entry<Character, Integer> o1, Entry<Character, Integer> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }
}

class Node implements Comparable<Node> {
    Node left = null;
    Node right = null;
    int value;
    boolean isLeaf = false;
    char symbol;

    Node(Node left, Node right, int value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    Node(char symbol, int value) {
        this.isLeaf = true;
        this.symbol = symbol;
        this.value = value;
    }

    @Override
    public int compareTo(Node o) {
        return this.value - o.value;
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

    private static PriorityQueue<Node> createQueue(Map<Character, Integer> alphabet) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (char c : alphabet.keySet()) {
            queue.offer(new Node(c, alphabet.get(c)));
        }
        return queue;
    }

    private static HashMap<Character, String> generateFixedLengthCodes(String alphabet) {
        HashMap<Character, String> codes = new HashMap<>();

        // Generate fixed-length codes for each character in the alphabet
        int codeLength = (int) Math.ceil(Math.log(alphabet.length()) / Math.log(2));
        int codeNumber = 0;
        for (char ch : alphabet.toCharArray()) {
            String code = Integer.toBinaryString(codeNumber);

            // Pad leading zeros to make the code fixed length
            while (code.length() < codeLength) {
                code = "0" + code;
            }

            codes.put(ch, code);
            codeNumber++;
        }

        return codes;
    }

    private static Node generateHuffmanCodes(PriorityQueue<Node> queue) {
        while (queue.size() > 1) {
            Node a = queue.poll();
            Node b = queue.poll();
            queue.offer(new Node(a, b, a.value + b.value));
        }
        return queue.poll();
    }

    private static void printHuffmanCodes(Node node, String code) {
        if (node == null)
            return;
        if (node.isLeaf)
            System.out.println(node.symbol + ": " + code);
        printHuffmanCodes(node.left, code + "0");
        printHuffmanCodes(node.right, code + "1");
    }

    private static void generateMap(Node node, HashMap<Character, String> codes, String code) {
        if (node == null)
            return;
        if (node.isLeaf)
            codes.put(node.symbol, code);
        generateMap(node.left, codes, code + "0");
        generateMap(node.right, codes, code + "1");
    }

    private static Map<Character, Integer> generateAlphabet(ArrayList<String> fileLines) {
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
        return sortByComparator(alphabet);
    }

    private static void printMenu() {
        System.out.println("Menu");
        System.out.println("1. Открыть текстовый файл");
        System.out.println("2. Вывести содержимое текстового файла");
        System.out.println("3. Вывести символы алфавита с указанием частоты их появления в файле");
        System.out.println("4. Сгенерировать коды фиксированной длины");
        System.out.println("5. Сгенерировать коды Хаффмана");
        System.out.println("6. Сжать файл с кодами фиксированной длины");
        System.out.println("7. Сжать файл с кодами Хаффмана");
        System.out.print("> ");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<String> fileLines = new ArrayList<>();
        Map<Character, Integer> alphabet = new LinkedHashMap<>();
        HashMap<Character, String> fixedLengthCodes = new HashMap<>();
        HashMap<Character, String> huffmanCodes = new HashMap<>();
        int ans = 0;
        do {
            printMenu();
            ans = in.nextInt();
            switch (ans) {
                case 1 -> {
                    System.out.print("Введите путь к текстовому файлу: ");
                    File file = Paths.get(in.next()).toFile();
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String str;
                        while ((str = br.readLine()) != null)
                            fileLines.add(str);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("Файл открыт и готов к работе");
                }
                case 2 -> {
                    for (String s : fileLines)
                        System.out.println(s);
                }
                case 3 -> {
                    alphabet = generateAlphabet(fileLines);
                    for (char c : alphabet.keySet())
                        System.out.println(c + ": " + alphabet.get(c));
                }
                case 4 -> fixedLengthCodes = generateFixedLengthCodes(alphabet.keySet().toString());
                case 5 -> {
                    PriorityQueue<Node> queue = createQueue(alphabet);
                    generateMap(generateHuffmanCodes(queue), huffmanCodes, "");
                }
                case 6 -> {}
                case 7 -> {}
                case 8 -> System.out.println("Завершение работы");
                default -> System.out.println("Неверная команда!");
            }
        } while (ans != 8);
    }
}