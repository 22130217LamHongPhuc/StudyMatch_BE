package com.example.demo.p1;

import java.util.*;

public class C {
    // dao nguoc mang
    public static int[] reverseArr(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        int[] result = new int[arr.length];
        for (left = 0; left <= right; left++) {
            result[left] = arr[right];
            result[right] = arr[left];
            right--;
        }
        return result;
    }

    public static int[] reverseArr2(int[] arr) {
        int left = 0;
        int right = arr.length - 1;
        while (left < right) {
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
        return arr;
    }
    // kiem tra mang co tang dan hay khong

    public static boolean checkArr(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    return false;

                }
            }
        }
        return true;
    }

    //dem so lan xuat hien cua 1 so
    public static int countCheck(int[] arr, int target) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                count++;
            }
        }
        return count;
    }

    // tim so lon thu hai
    public static int checkNumber(int[] arr) {
        int left = 1;
        int max = arr[0];
        while (left < arr.length) {
            if (arr[left] > max) {
                max = arr[left];
            }
            left++;
        }


        return 0;

    }

    public static int check(int[] arr) {
        Arrays.sort(arr);
        for (int i = arr.length - 2; i >= 0; i--) {
            if (arr[i] == arr[arr.length - 1]) continue;
            return arr[i];
        }
        throw new RuntimeException("đã xảy ra lỗi");
    }

    //xoa phan tu tai vi tri index
    public static int[] remove (int[] arr, int index){
        int[] result = new int[arr.length-1];
        boolean newIndex=false;
        for(int i =0; i< arr.length; i++){
            if(i == index) {
                newIndex = true;
                continue;
            }
            if(!newIndex){
                result[i] = arr[i];
            }else if(newIndex){
                result[i-1] = arr[i];
            }
        }
        return  result;
    }

    // chen phan tu vao vi tri index
    public static int[] insert(int[] arr, int index, int target){
        int[] result = new int[arr.length+1];
        for(int i =0; i<index; i++){
            result[i] = arr[i];
        }
        result[index] = target;
        for (int j =index+1; j <=arr.length; j ++ ){
            result[j] = arr[j-1];
        }
        return result;
    }

    // xoa cac phan tu bang voi target
    public static int[] removeItem(int [] arr, int target){
        Set<Integer> set = new LinkedHashSet<>();
        for(int i =0; i<arr.length; i++){
            if(arr[i] == target) continue;
            set.add(arr[i]);
        }
        int[] result = new int[set.size()];
        int index=0;
       for(Integer item: set){
           result[index] = item;
           index++;
       }
       return result;
    }

    // gộp 2 mảng
    public static int[] merge (int[] arr1, int[] arr2){
        int[] result = new int[arr1.length+ arr2.length];
        for(int i =0; i< arr1.length; i++){
            result[i] = arr1[i];
        }
        int index = arr1.length;
        for(int i =0; i<arr2.length; i++){
            result[index] = arr2[i];
            index++;
        }
        return  result;
    }
    //loai bo phan tu trung
    public static int[] removeRepeat(int[] arr){
        Set< Integer> set = new LinkedHashSet<>();
        for(int i =0; i< arr.length; i++){
            set.add(arr[i]);
        }
        int[] result = new int[set.size()];
        int index =0;
        for(Integer item : set){
            result[index] = item;
            index++;
        }
        return result;
    }
    // tim phan tu xuat hien nhieu nhat
    public static  int findItem(int[] arr){
        Map<Integer, Integer> map = new HashMap<>();
        for(int i =0; i<arr.length; i++){
           if(!map.containsKey(arr[i])){
               map.put(arr[i], 1);
           }
           else{
               map.put(arr[i], map.get(arr[i])+1);
           }
        }
        int max =0 ;
        int item = arr[0];
        for(Map.Entry<Integer, Integer> entry : map.entrySet()){
            if(entry.getValue() > max){
                max = entry.getValue();
                item = entry.getKey();
            }
        }
        return  item;
    }


    public static void main(String[] args) {
//        System.out.println(
//                Arrays.toString( removeItem(new int[]{-100, 0, 3, 5}, 5)));
        boolean[] bool = new boolean[3];

        System.out.println(findItem(new int []{3, 5, 5, 3, 3,5,5}));
    }


}