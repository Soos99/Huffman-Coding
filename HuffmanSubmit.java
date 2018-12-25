// Import any package as required

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class HuffmanSubmit implements Huffman {

    // Feel free to add more methods and variables as required.

    public void encode (String inputFile, String outputFile, String freqFile) {
        try {
            // TODO: Your code here
            BinaryOut out = new BinaryOut(outputFile);
            BinaryIn in = new BinaryIn(inputFile);
            BufferedWriter freq = new BufferedWriter(new FileWriter(freqFile));
            int[] store = new int[256];
            String s = in.readString();
            char[] ch = s.toCharArray();

            for (char a : ch) {
                store[(int) a]++;
            }
            for (int i = 0; i < 256; i++) {
                if (store[i] > 0) {
                    freq.write(convertToBin(i) + ":" + store[i]);
                    freq.newLine();
                }
            }
            freq.close();

            Node root = buildHuffman(store);
            String[] str = new String[256];
            convertToHuffman(root, str, "");
            for (char a : ch) {
                String s1 = str[(int) a];
                for (int i = 0; i < s1.length(); i++) {
                    if (s1.charAt(i) == '0')
                        out.write(true);
                    else
                        out.write(false);
                }
            }
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void decode(String inputFile, String outputFile, String freqFile) {
        try{
            // TODO: Your code here
            BinaryIn in = new BinaryIn(inputFile);
            BinaryOut out = new BinaryOut(outputFile);
            File file = new File(freqFile);
            Scanner sc = new Scanner(file);
            int[] store = new int[256];
            while (sc.hasNext()) {
                String str = sc.nextLine();
                String[] s = str.split(":");
                String s1 = "";
                String s2 = "";
                for (int i = 0; i < s.length; i++) {
                    if (!(s[i].equals(""))) {
                        if (!(s1.equals("")))
                            s2 += s[i];
                        else
                            s1 += s[i];
                    }
                }
                store[convertToNum(s1)] = Integer.parseInt(s2);
            }

            Node root = buildHuffman(store);

            while (!(in.isEmpty())) {
                Node node = root;
                while (!(node.isLeaf())) {
                    if (in.isEmpty())
                        break;
                    boolean flag = in.readBoolean();
                    if (flag)
                        node = node.left();
                    else
                        node = node.right();
                }
                out.write(node.getChar());
            }
            out.flush();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    //convert to Huffman-coding from normal character
    public void convertToHuffman(Node node, String[] str, String s){
        if (node.isLeaf()){
            str[(int) node.getChar()] = s;
            return;
        }
        convertToHuffman(node.left(),str,s + "0");
        convertToHuffman(node.right(),str, s + "1");
    }

    // build Huffman Tree
    public Node buildHuffman (int[] x){
        ArrayList<Node> maxHeap = new ArrayList<>();
        for (int i = 0; i < x.length; i++){
            if (x[i] > 0){
                insert(maxHeap, new Node((char) i, x[i], null, null));
            }
        }
        while (maxHeap.size() != 1){
            Node left = maxHeap.remove(maxHeap.size() - 1);
            Node right = maxHeap.remove(maxHeap.size() - 1);
            insert(maxHeap, new Node('\0', left.freq() + right.freq(), left, right));
        }
        return maxHeap.get(0);

    }

    //insert node into sorted array
    public void insert (ArrayList<Node> arr, Node node){
        int index = 0;
        for (int i = 0; i < arr.size() && node.freq() < arr.get(i).freq(); i++){
            index++;
        }
        arr.add(index,node);
    }

    // convert from binary sequence to number
    public int convertToNum (String s){
        int coefficient = 1;
        int num = 0;
        for (int i = s.length() - 1; i >= 0; i--){
            if (s.charAt(i) == '1')
                num += coefficient;
            coefficient *= 2;
        }
        return num;
    }

    // convert to binary sequence from number
    public String convertToBin(int num){
        assert num < 0 || num > 255: "This number is out of range";
        String s = "";
        int i = 128;
        while (s.length() < 8){
            if (num >= i){
                s += "1";
                num -= i;
            }
            else {
                s += "0";
            }
            i /= 2;
        }
        return s;
    }

    //Node class
    class Node {
        private Node left;
        private Node right;
        private int freq;
        private char a;
        Node (char a, int freq, Node left,Node right) {
            this.a = a;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf (){
            return (left == null && right == null);
        }
        char getChar() {
            return a;
        }
        Node left() {
            return left;
        }
        Node right(){
            return right;
        }
        int freq() {
            return freq;
        }
    }

    //Main method
    public static void main(String[] args) throws IOException {
        Huffman huffman = new HuffmanSubmit();
        //huffman.encode("alice30.txt", "alice31.txt", "freq.txt");
        //huffman.decode("alice31.txt", "alice32.txt", "freq.txt");
        System.out.println("Hi, please enter the command in this format:");
        System.out.println("  encode/decode inputFile outputFile freqFile");
        System.out.println("       For example: encode ur.jpg ur.enc freq.txt");
        Scanner sc = new Scanner(System.in);
        String command = sc.next();
        if (command.equals("encode")){
            huffman.encode(sc.next(),sc.next(),sc.next());
        }
        else if (command.equals("decode")){
            huffman.decode(sc.next(),sc.next(),sc.next());
        }
        else {
            System.out.println("Sorry, the command is invalid");
        }

        // After decoding, both ur.jpg and ur_dec.jpg should be the same.
        // On linux and mac, you can use `diff' command to check if they are the same.
    }

}