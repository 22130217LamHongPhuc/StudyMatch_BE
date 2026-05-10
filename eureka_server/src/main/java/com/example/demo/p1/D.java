package com.example.demo.p1;

import java.util.Arrays;


class D {
    public static int[] bubbleSort(int arr[]) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }

    //   public static int[] insertionSort(int arr[]){
//       for(int i = 1; i<arr.length; i++){
//           int key = arr[i];
//           int j;
//           for ( j = i-1; j >=0; j--){
//               if(arr[j]>key){
//                   arr[j + 1] = arr[j];
//               }
//               else{
//                   break;
//               }
//           }
//           arr[j + 1] = key;
//       }
//       return arr;
//   }
    public static int[] insertSort(int[] arr) {
        int indexCurrent = 1;
        while (indexCurrent < arr.length) {

            for (int i = indexCurrent - 1; i >= 0; i--) {

                if (arr[indexCurrent] > arr[i]) {
                    int temp = 0;
                    temp = arr[indexCurrent];
                    arr[indexCurrent] = arr[i];
                    arr[i] = temp;
                    break;
                }
            }
            indexCurrent++;
        }
        return arr;
    }

    public static int[] selectionSort(int[] arr) {
        for(int i =0; i< arr.length; i++){
            int min = arr[i];
            int index= i; ;
            for(int j = i; j<arr.length; j++){
                if(arr[j]<min){
                    min = arr[j];
                    index = j;
                }
            }
            int temp = arr[i];
            arr[i] = min;
            arr[index] = temp;
        }
        return arr;
    }

    public boolean linearSearch(int[] arr, int target) {
        for (int i = 0; i< arr.length; i++) {
            if(arr[i] == target){
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        System.out.println(Arrays.toString(selectionSort(new int[]{4, 2, 5, 1, 9})));
//        D.bubbleSort(bubbleSort(new int[]{1,2,3,4,5}));
    }
}
