package me.vinachiong.kotlin.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 杨辉三角
 * 给定一个非负整数 numRows，生成杨辉三角的前 numRows 行。
 */
public class Array118 {

    public static List<List<Integer>> generate(int numRows) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> last = null;
        for (int row = 0; row < numRows; row++) {
            int size = row + 1, start = 0, end = size - 1;
            List<Integer> curr = new ArrayList<>(size);
            System.out.println("row = " + row);
            for (int j = start; j < size; j++) {
                System.out.println("j = " + j);
                if (j == start || j == end) {
                    curr.add(1);
                } else if (last != null) {
                    int num = last.get(j - 1) + last.get(j);
                    curr.add(num);
                }
            }
            result.add(curr);
            last = curr;
        }
        return result;
    }


    public static void main(String[] args) {
        try{
            for (List<Integer> lines : generate(30)) {
                System.out.println(lines + ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
