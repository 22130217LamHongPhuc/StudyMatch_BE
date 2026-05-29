package com.example.demo.p1;

import java.util.*;


public class A {
    public static void count(String input) {
        int digit = 0;
        int letter = 0;
        int specical = 0;
//            String[] arr = Arrays.


        StringBuilder builder = new StringBuilder(input);

//            builder.append(input);
        System.out.println(builder.reverse());

        for (int i = 0; i < input.length(); i++) {
            Character character = input.charAt(i);

            if (Character.isDigit(character)) {
                letter++;
            } else if (Character.isLetter(character)) {
                digit++;

            } else if (!Character.isWhitespace(character)) {
                specical++;
            }

        }
        System.out.println(digit + " : " + +letter + " : " + specical);

    }

    public static String reverseString(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = input.length() - 1; i >= 0; i--) {
            char c = input.charAt(i);
            result.append(c);
        }

        System.out.println(result.toString());
        return result.toString();
    }

    public static boolean paliadrome(String input) {
        int right = input.length() - 1;
        for (int i = 0; i <= input.length() / 2; i++) {
            if (input.charAt(i) != input.charAt(right)) {
                System.out.println("nahyr vào false");
                return false;
            }
            right--;
        }
        System.out.println("két quả true");
        return true;
    }

    public static String convert(String input) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i <= input.length() - 1; i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append(Character.toLowerCase(c));
            }
            if (Character.isLowerCase(c)) {
                builder.append(Character.toUpperCase(c));
            } else {
                builder.append(c);
            }
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    public static Map<Character, Integer> countAppear(String input) {
        HashMap<Character, Integer> map = new HashMap<>();
        char[] character = input.toCharArray();
        for (char c : character) {
            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                map.put(c, map.get(c) + 1);
            }

        }
        System.out.println(map);
        return map;
    }

    public static Map<Character, Integer> countMaxAppear(String input) {
        HashMap<Character, Integer> map = new HashMap<>();
        char[] character = input.toCharArray();
        for (char c : character) {
            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                map.put(c, map.get(c) + 1);
            }

        }
        int maxCount = 0;
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
            }


        }
        System.out.println("maxx value " + maxCount);
        System.out.println(map);
        return map;
    }

    // tìm ki tu dau tien khong duoc lap lai
    public static Character findCharacter(String input) {
        Map<Character, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                map.put(c, map.get(c) + 1);
            }
        }
        Character result = null;
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        System.out.println(result);
        return null;
    }

    public static Map<Character, Integer> countLetter(String input) {
        HashMap<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                map.put(c, map.get(c) + 1);
            }

        }
        return map;
    }

    public static boolean anagram(String input1, String input2) {
        Map<Character, Integer> map1 = countLetter(input1);
        Map<Character, Integer> map2 = countLetter(input2);
        return map1.equals(map2);
    }

    // in hoa kí tự đầu tiên của mỗi từ
    public static String upper(String input) {
        String[] arr = input.split("\\s+");

        for (int i = 0; i < arr.length; i++) {
            char c = arr[i].charAt(0);
            Character character = Character.toUpperCase(c);
            arr[i] = arr[i].replaceFirst(String.valueOf(c), String.valueOf(character));
        }
        return String.join(" ", arr);
    }

    // dao nguoc cac tu
    public static String reverseS(String input) {
        String[] arr = input.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (int i = arr.length - 1; i >= 0; i--) {
            builder.append(arr[i]);
            builder.append(" ");
        }
        return builder.toString();
    }

    //dao nguoc cac tu
    public static String reverse(String input) {
        String[] arr = input.split("\\s+");
        StringBuilder buider = new StringBuilder();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {

            buider.append(b.append(arr[i]).reverse());
            b.setLength(0);
            buider.append(" ");
        }
        return buider.toString();
    }

    //tim tu dai nhat trong cau
    public static void maxLength(String input) {
        List list = new ArrayList();
        String[] arr = input.split("\\s+");
        int index = 0;
        int maxLength = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() > maxLength) {
                maxLength = arr[i].length();
                index = i;
            }
        }
        list.add(arr[index]);
        for (int i = 0; i < arr.length; i++) {
            if (index == i) continue;
            if (arr[i].length() == maxLength) {
                list.add(arr[i]);
            }
        }

        if (list.size() > 1) {
            System.out.println(list.size() + " " + list);
            return;
        }
        System.out.println(list);
    }

// dem so nguyen am va phu am
    public static void countVowels(String input) {
        if(input.trim().length()==0) throw  new RuntimeException("xảy ra lõi");

        int vowel = 0;
        int consonant = 0;
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isLetter(input.charAt(i))) continue;
            char curr = input.toLowerCase().charAt(i);
            if (curr == 'u' || curr == 'e' || curr == 'o' || curr == 'a' || curr == 'i'
            ) {
                vowel++;
            } else {
                consonant++;
            }
        }
        System.out.println("phu am" + vowel + " nguyen am" + consonant);
    }
    // loai tat ca space cua chuoi
    public static String removeSpace(String input){
        return input.replaceAll("\\s+", "");
    }

    // kiem tra 2 chuoi co phai la dao chuoi voi nhau khong
    public static boolean checkReverse(String input1, String input2){
        if(input1.length() != input2.length()) throw new RuntimeException("2 chuoi ko bang nhau");
        // cach 1
//        String builder = new StringBuilder(input2).reverse().toString();
//        return input1.equals(input2);

        // cach 2
        int right = input2.length() -1;
        for( int i =0; i< input1.length(); i++){
            if(input1.charAt(i) != input2.charAt(right)){
                return false;
            }
            right--;
        }
        return true;
    }
    // xoa phan tu dau tien va cuoi cung
    public static String removeFAL (String input){
        StringBuilder builder = new StringBuilder(input);
        for(int i =1; i < input.length()-1; i++){
            builder.append(input.charAt(i));
        }
        return builder.toString();
    }

    // dem so tu trong string
    public static int countWord (String input){
        String[] arr = input.split("\\s+");
        return arr.length;
    }


    // kiem tra 1 chuoi co so khong
    public static boolean checkDigit(String input){
        for(int i =0; i < input.length(); i++){
            if(Character.isDigit(input.charAt(i))){
                return false;
            }
        }
        return true;
    }

    //kiem tra palindrome bang true pointer
    public static boolean checkPalindrome(String input){
        int left =0;
        int right = input.length()-1;
        while (left < right){
            if(input.charAt(left) != input.charAt(right)){
                return false;
            }
            left++;
            right--;
        }
        return true;
    }



    public static void main(String[] args) {
      boolean a = true;
      boolean b = false;
        System.out.println(a == b | a==b);

    }

}

