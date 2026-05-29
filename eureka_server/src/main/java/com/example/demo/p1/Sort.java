package com.example.demo.p1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Sort {
    private static final Logger log = LoggerFactory.getLogger(Sort.class);

    public static int[] bubbleSort(int[] arr) {
        while (true) {
            boolean swapped = false;
            for (int i = 0; i < arr.length - 1; i++) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return arr;
    }

    public static int[] selectionSort (int[]arr){
       for (int i =0; i< arr.length; i++){
           int index = i;
           int min = arr[i];

           for (int j =i +1; j < arr.length; j ++){
              if(arr[j] < min){
                  min = arr[j];
                  index = j;
              }
           }
           if(arr[i] > min){
               int temp = arr[i];
               arr[i] = min;
               arr[index] = temp;
           }

       }
        return arr;
    }

    public static int[]  insertionSort(int[] arr){
        for(int i =1; i< arr.length; i++){
            int min = arr[i];
            int index = i;
            for(int j = i; j >=0; j--){
                if(arr[j]>arr[j-1] ){

                    continue;
                }
                min = arr[j];
                index = j;
            }

        }
        return arr;
    }


    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 4, 2};

        selectionSort(arr);

        System.out.println(Arrays.toString(arr));
    }

}
