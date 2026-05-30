package com.example.demo.p1;

import java.util.Arrays;

public class Arr_2_Exam {


    //concat array
    public static int[] concatArr(int[]arr){
        int[] result = new int[arr.length*2];
        int i =0;
        int j = 0;
        while (i<arr.length*2){
            if(i< arr.length){
                result[i] = arr[i];
            }
            else{
                result[i] = arr[j];
                j ++;
            }
            i++;
        }
        return result;
    }
    // moi phan tu tai vi tri i bang tong cac phan tu tu dau mang den vi tri i
    //Input: nums = [1,2,3,4]
    //Output: [1,3,6,10]
    public static int[] totalElement (int[] arr){
        int[] result = new int[arr.length];
        int total = arr[0];
        for(int i = 0; i< arr.length; i++){
            total += arr[i];
            result[i] = total;
        }
        return  result;
    }

//    Cho một mảng nums. Với mỗi phần tử nums[i], hãy đếm xem trong mảng có bao nhiêu số nhỏ hơn nó.
    //Trả về một mảng kết quả.
    //Input: nums = [8,1,2,2,3]
    //Output: [4,0,1,1,3]
    public static int[] countElement(int[] arr){
        int[] result = new int[arr.length];
        for(int i = 0; i < arr.length; i++){
            int value = arr[i];
            int count = 0;
            for(int j = 0; j < arr.length; j ++){
                if(arr[j] < value){
                    count++;
                }
            }
            result[i] = count;
        }
        return result;
    }
//    Cho một mảng số nguyên nums và một số nguyên target.
//    Hãy tìm 2 vị trí trong mảng sao cho tổng của 2 phần tử đó bằng target.
//    Input: nums = [2,7,11,15], target = 9
//    Output: [0,1]
    public static int[] findArr (int[] arr, int target){
        int[] result = new int[2];
        for(int i = 0; i< arr.length-1; i++){
            for(int j = i+1; j< arr.length; j++){
                if (arr[i] + arr[j] == target) {
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }
        throw new RuntimeException("Not Found");
    }

    // kiem tra phan tu trong mang co bi trung hay khong
    // neu co bi trung tra ve true neu khong thi tr ve false
    public static  boolean checkDuplicate(int[] arr){
        for(int i =0; i< arr.length-1; i++){
            for(int j = i+1; j< arr.length; j++){
                if(arr[i] == arr[j]) return true;
            }
        }
        return false;
    }

    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;

        int k = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[k - 1]) {
                nums[k] = nums[i];
                k++;
            }
        }

        return k;
    }



    public static void main(String[] args) {
        int[] arr = new int[]{2,2,7,11,15};
//        System.out.println(Arrays.toString(findArr(arr, 9)));
        System.out.println(removeDuplicates(arr));
    }
}
