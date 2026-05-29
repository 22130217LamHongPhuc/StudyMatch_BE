package com.example.demo.p1;

import java.util.*;

public class ArrExam {

    // tim so lon nhat trong mang
    public static int maxNumber (int[] arr){
        int max = arr[0];
        for(int i =0; i< arr.length; i++){
            if(arr[i]> max){
                max = arr[i];
            }
        }
        return max;
    }
    // dem so chan , le
    public static void count(int[] arr){
        int odd =0;
        int even=0;
        for(int i =0; i< arr.length; i++){
            if(arr[i] %2==0){
                even+=1;
            }
            else{
                odd +=1;
            }

        }
        System.out.println("even: "+ even + " odd: "+ odd);
    }
    // dao nguoc mang
    public static int[] reverseArr (int[] arr){
        int[] result = new int[arr.length];
        int index = 0;
        for(int i =arr.length-1; i>= 0; i--){
            result[index] = arr[i];
            index++;
        }
        return result;
    }

    // tim vi tri phan tu trong mang
    public static int findIndex (int[] arr, int number){
        for(int i =0; i< arr.length; i++){
            if(arr[i] == number){
                return i;
            }
        }
        throw new IllegalArgumentException(" ko tim thay phan tu");
    }
    // sap xep mang tang dàn
    public static int[] sortArr (int[] arr){
       for(int i = 0; i< arr.length; i++){
           for(int j =i; j < arr.length; j ++){
               if (arr[i] > arr[j]) {
                   int temp = arr[i];
                   arr[i] = arr[j];
                   arr[j] = temp;
               }
           }
       }
       return arr;
    }
    // tim phan tu lon thu 2
    public static int findSecondArr(int[] arr){
        int max = Integer.MIN_VALUE;
        int second = Integer.MIN_VALUE;
        for (int i = 0; i<arr.length; i++){
            if(arr[i] > max){
                second = max;
                max = arr[i];
            }else if(arr[i] < max && arr[i] > second){
                second = arr[i];
            }
        }
        return second;
    }


    public static void main(String[] args) {
        int[] arr = {9, -99, 5, 100};
//        System.out.println(reverseArr(new int[]{9, -99, 5, 100}));
        System.out.println(Arrays.toString(sortArr(new int[]{9, -99, 5, 100})));
//        System.out.println(findIndex(arr, 8));
//        count(arr);

    }

}
