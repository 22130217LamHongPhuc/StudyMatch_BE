package com.example.demo.p1;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StringExam {
    //dem chu hoa, chu thuong, ki tu dac biet
    public static void countLetterAnDigit(String input) {
        if (input.length() == 0) throw new IllegalArgumentException("input khong du ki tu");
//        if(input ==null) throw  new IllegalArgumentException("khong duoc co gia tri null");
        int letter = 0;
        int digit = 0;
        int special = 0;
        for (int i = 0; i < input.length(); i++) {
            Character character = input.charAt(i);
            if (Character.isLetter(input.charAt(i))) {
                letter += 1;
            } else if (Character.isDigit(input.charAt(i))) {
                digit += 1;
            } else if (!Character.isSpaceChar(input.charAt(i))) {
                special += 1;
            }
        }
        System.out.println("letter: " + letter + " digit: " + digit + "special: " + special);
    }


    // bai tap dao nguoc chuoi
    public static String reverseString(String input) {
        StringBuilder builder = new StringBuilder(input).reverse();
        return builder.toString();
    }

    public static String reverseS(String input) {
        StringBuilder builder = new StringBuilder();
        for (int i = input.length() - 1; i >= 0; i--) {
            builder.append(input.charAt(i));
        }
        return builder.toString();
    }

    // kiem tra 2 chuoi co doi xung khong
    public static boolean check(String input1, String input2) {
        int j = input2.length() - 1;
        for (int i = 0; i < input1.length(); i++) {

            if (input1.charAt(i) != input2.charAt(j)) return false;
            j--;
        }
        return true;
    }

    // kiem tra chuoi co phai la palidrome khong
    public static boolean isPalidrome(String input) {
        if (input == null || input.length() == 0) throw new IllegalArgumentException("  input khong hop le");
        int left = 0;
        int right = input.length() - 1;
        while (left < right) {
            if (input.charAt(left) != input.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    // chuyen chu hoa thanh chu thuong va nguoc lai
    public static String convertString (String input)   {
        if(input == null || input.trim().length()==0){
            throw  new IllegalArgumentException("input khong hop le");
        }
        StringBuilder builder = new StringBuilder();
        for (int i =0; i< input.length(); i++){
            Character character = input.charAt(i);
            if( !Character.isLetter(character)) {
                builder.append(character);
                continue;
            }
            if(Character.isUpperCase(input.charAt(i))){
                builder.append(Character.toLowerCase(character));
            }else if (Character.isLowerCase(input.charAt(i))){
                builder.append(Character.toUpperCase(character));
            }
        }
        return builder.toString();

    }

    // dem so lan xuat hien cua tung ki tu
    public static Map<Character, Integer> count (String input){
        Map< Character, Integer> map = new HashMap<>();
        for(int i =0; i< input.length(); i++){
            Character character = input.charAt(i);
            if(map.get(character) == null){
                map.put(character, 1);
            }
            else{
                map.put(character, map.get(character)+1);
            }
        }
        return map;
    }

    // tim ki tu dau tien khong bi lap
    public static Character findCharater(String input){
        Map<Character, Integer> map = new LinkedHashMap<>();
        for(int i =0; i< input.length(); i++){
            Character character = input.charAt(i);
            character = Character.toLowerCase(character);
            if(!Character.isLetter(character)) continue;
            if(map.get(character) == null){
                map.put(character, 1);
            }
            else{
                map.put(character, map.get(character)+1);
            }
        }
        Character character = firstCharacter(map);
        if(character== null) return null;
        return character;
    }
    public static  Character firstCharacter(Map<Character, Integer> map){
        for (Map.Entry<Character, Integer> entry : map.entrySet()){
            if(entry.getValue() !=1) continue;
            return entry.getKey();
        }
        return null;
    }

    //kiem tra 2 chuoi co cung ki tu va so lan xuat hien, khac thu tu sap xep
    public static boolean checkTwoString (String input1, String input2){
        Map<Character, Integer> map = map(input1);
        Map<Character, Integer> map2 = map(input2);
        return map.equals(map2);
    }
    public static Map< Character, Integer> map (String input){
        Map<Character, Integer> map = new LinkedHashMap<>();
        for(int i =0; i< input.length(); i++){
            Character character = input.charAt(i);
            character = Character.toLowerCase(character);
            if(!Character.isLetter(character)) continue;
            if(map.get(character) == null){
                map.put(character, 1);
            }
            else{
                map.put(character, map.get(character)+1);
            }
        }
        return map;
    }

    public static String converString (String input){
        String[] arr = input.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for(int i =0 ; i< arr.length; i++){
            String item = arr[i];
            for(int j =0; j<item.length(); j++){
                if(j ==0){
                    builder.append(Character.toUpperCase(arr[i].charAt(0)));
                    continue;
                }
                builder.append(arr[i].charAt(j));

            }
            builder.append(" ");
        }
        return builder.toString();
    }

    // tim tu dai nhat trong cau
    public static String checkLonger(String input){
        String[] arr = input.split("\\s+");
        String letter = arr[0];
        int longLetter = arr[0].length();
        for(int i =0; i< arr.length; i++){
            if(arr[i].length() > longLetter){
                letter = arr[i];
                longLetter = arr[i].length();
            }
        }
        return letter;
    }








    public static void main(String[] args) {
//        Map< Character, Integer> map = new HashMap<>();
//        map.put('1', 1);
//        System.out.println(map.get('o'));
//
        String input = "madam 1 Helo, mm";
        String input2 = "olleh1";

        System.out.println(converString(input));
    }
}
