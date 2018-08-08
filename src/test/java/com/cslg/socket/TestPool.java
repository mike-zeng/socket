//package com.cslg.socket;
//
//import com.cslg.socket.common.Pool;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Scanner;
//
//public class TestPool {
//
//    @Test
//    public void test() {
//        Pool.threadPool.execute(() -> {
//            System.out.println(Thread.currentThread().getName());
//        });
//
//        System.out.println(Math.random());
//        for(int j = 0; j < 10; j++) {
//            String[] s = {"工一", "工二", "综教"};
//            int i = (int) (Math.random() * s.length);
//            System.out.println(s[i]);
//        }
//
//    }
//
//    @Test
//    public void test1() {
//        Integer[] temps = new Integer[]{34,35,33,25,44,45,46,17};
//        List<Integer> list = new ArrayList<>();
//        int[] a = new int[temps.length];
//        boolean k;
//        for(int i = 0; i < temps.length; i++) {
//            k = true;
//            for(int j = i + 1; j < temps.length; j++) {
//                if (temps[j] > temps[i]) {
//                    a[i] = j - i;
//                    list.add(j - i);
//                    k = false;
//                    break;
//                }
//            }
//            if(k) {
//                a[i] = 0;
//            }
//        }
//        System.out.println(Arrays.asList(a));
//    }
//
//    @Test
//    public void test2() {
//        int m = 7;
//        int n = 3;
//        int s = m * n;
//        if (m < n) {
//            int k = m;
//            m = n;
//            n = k;
//        }
//        int min = n;
//        while (min > 0) {
//            if (m % min == 0 && n % min == 0) {
//                break;
//            }
//            min--;
//        }
//        System.out.println(min);
//        System.out.println(s / (min * min));
//    }
//
//    @Test
//    public void test3() {
//        char[][] c = {{'+', 'o', 'o', '+'}, {'o', 'o', 'o', '+'}, {'o', 'o', 'o', '+'}};
//        int sum = 0;
//        char [][] f = new char[c.length + 2][c[0].length + 2];
//        for(int i = 0; i < f.length; i++) {
//            for(int j = 0; j < f[0].length; j++) {
//                f[i][j] = 'o';
//            }
//        }
//        for(int i = 0; i < c.length; i++) {
//            for (int j = 0; j < c[0].length; j++) {
//                f[i + 1][j + 1] = c[i][j];
//            }
//        }
//
//        for(int i = 1; i < f.length -1; i++) {
//            for(int j = 1; j < f[0].length -1; j++) {
//                if(f[i][j] == '+') {
//                    if(f[i - 1][j] != '+'
//                        && f[i + 1][j] != '+'
//                        && f[i][j - 1] != '+'
//                        && f[i][j + 1] != '+') {
//                        sum ++;
//                    } else {
//                        f[i][j] = 'o';
//                    }
//                }
//            }
//        }
//        System.out.println(sum);
//    }
//
//
//}
