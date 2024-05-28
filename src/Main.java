import java.io.*;
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

    private static HashMap<Character, String> generateFixedLengthCodes(Set alphabet) {
        HashMap<Character, String> codes = new HashMap<>();
        int codeLength = (int) Math.ceil(Math.log(alphabet.size()) / Math.log(2));
        int codeNumber = 0;
        for (Object ch : alphabet) {
            String code = Integer.toBinaryString(codeNumber);
            while (code.length() < codeLength)
                code = "0" + code;
            codes.put(ch.toString().charAt(0), code);
            codeNumber++;
        }
        System.out.println("Коды фиксированной длины сгенерированы");
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

    private static void compress(ArrayList<String> fileLines, HashMap<Character, String> codes) {
        Scanner in = new Scanner(System.in);
        int count = 0;
        System.out.print("Введите название файла: ");
        try (FileWriter fw = new FileWriter(in.next())){
            for (String str : fileLines) {
                for (char c : str.toCharArray()) {
                    String code = codes.get(c);
                    for (char c1 : code.toCharArray()) {
                        fw.write(c1);
                        count++;
                    }
                }
            }
            fw.flush();
            System.out.println("Кол-во символов в файле: " + count);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void printMenu() {
        System.out.println("Menu");
        System.out.println("1. Открыть текстовый файл");
        System.out.println("2. Вывести содержимое текстового файла");
        System.out.println("3. Сгенерировать алфавит с указанием частоты появления символов в файле");
        System.out.println("4. Сгенерировать коды фиксированной длины");
        System.out.println("5. Сгенерировать коды Хаффмана");
        System.out.println("6. Сжать файл с кодами фиксированной длины");
        System.out.println("7. Сжать файл с кодами Хаффмана");
        System.out.println("8. Выход из программы");
        System.out.print("> ");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        File file = null;
        ArrayList<String> fileLines = null;
        Map<Character, Integer> alphabet = new LinkedHashMap<>();
        HashMap<Character, String> fixedLengthCodes = new HashMap<>();
        HashMap<Character, String> huffmanCodes = new HashMap<>();
        int ans = 0;
        do {
            printMenu();
            ans = in.nextInt();
            switch (ans) {
                case 1 -> {
                    fileLines = new ArrayList<>();
                    System.out.print("Введите путь к текстовому файлу: ");
                    file = Paths.get(in.next()).toFile();
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
                    System.out.println("Алфавит сгенерирован");
                }
                case 4 -> {
                    fixedLengthCodes = generateFixedLengthCodes(alphabet.keySet());
                    for (char c : fixedLengthCodes.keySet().stream().toList())
                        System.out.println(c + ": " + fixedLengthCodes.get(c));
                    System.out.println("Коды фиксированной длины сгенерированы");
                }
                case 5 -> {
                    PriorityQueue<Node> queue = createQueue(alphabet);
                    generateMap(generateHuffmanCodes(queue), huffmanCodes, "");
                    for (char c : huffmanCodes.keySet())
                        System.out.println(c + ": " + huffmanCodes.get(c));
                    System.out.println("Коды Хаффмана сгенерированы");
                }
                case 6 -> compress(fileLines, fixedLengthCodes);
                case 7 -> compress(fileLines, huffmanCodes);
                case 8 -> System.out.println("Завершение работы");
                default -> System.out.println("Неверная команда!");
            }
        } while (ans != 8);
    }
}