//package com.cslg.socket;
//
//import org.junit.Test;
//
//import java.io.*;
//import java.net.Socket;
//
//public class SocketClient {
//
//    /**
//     * 字符串转成十六进制byte[] 类型数组
//     */
//    private byte[] hex2byte(String hex) {
//        String digital = "0123456789ABCDEF";
//        String hex1 = hex.replace(" ", "");
//        char[] hex2char = hex1.toCharArray();
//        byte[] bytes = new byte[hex1.length() / 2];
//        byte temp;
//        for(int p = 0; p < bytes.length; p++) {
//            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
//            temp += digital.indexOf(hex2char[2 * p + 1]);
//            bytes[p] = (byte) (temp & 0xff);
//        }
//        return bytes;
//    }
//
//    /**
//     *  将字符串编码成16进制数字,适用于所有字符（包括中文）
//     */
//    public String encode(byte[] bytes) {
//        try {
//            String hexString = "0123456789ABCDEF";
//            StringBuilder sb = new StringBuilder(bytes.length * 2);
//            for(byte k : bytes) {
//                sb.append(hexString.charAt((k & 0xf0) >> 4));
//                sb.append(hexString.charAt(k & 0x0f));
//            }
//            return sb.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            //logger.error(Thread.currentThread().getName(), "encode方法异常", e);
//            return null;
//        }
//    }
//
//    public void test() {
//        System.out.println(encode("FE".getBytes()));
//    }
//
//    @Test
//    public void clientTest() {
//        Socket socket = null;
//        try {
//            socket = new Socket("127.0.0.1", 10054);
//            OutputStream outputStream = socket.getOutputStream();
//            InputStream inputStream = socket.getInputStream();
//            outputStream.write(hex2byte("EE"));
//            int i = 0;
//            while (true) {
//                byte[] bytes = new byte[6];
//                //Thread.sleep(1000);
//                //outputStream.write(hex2byte("FE"));
//                //outputStream.write(hex2byte("FE"));
//                //bufferedWriter.flush();
//                inputStream.read(bytes);
//                outputStream.write(hex2byte("010302093BFFC7"));
//                System.out.println(encode(bytes));
//                //Thread.sleep(300);
//            }
//            //bufferedWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @Test
//    public void clientTest2() {
//        Socket socket = null;
//        try {
//            socket = new Socket("127.0.0.1", 10054);
//            OutputStream outputStream = socket.getOutputStream();
//            InputStream inputStream = socket.getInputStream();
//            outputStream.write(hex2byte("FE"));
//            int i = 0;
//            while (true) {
//                byte[] bytes = new byte[6];
//                //Thread.sleep(1000);
//                //outputStream.write(hex2byte("FE"));
//                //outputStream.write(hex2byte("FE"));
//                //bufferedWriter.flush();
//                inputStream.read(bytes);
//                outputStream.write(hex2byte("01030200017984"));
//                System.out.println(encode(bytes));
//                //Thread.sleep(300);
//            }
//            //bufferedWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
