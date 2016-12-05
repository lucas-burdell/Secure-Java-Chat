/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textchatv2.algorithms;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import textchatv2.SecuritySolution;

/**
 *
 * @author lucas.burdell
 */
public class TEA extends SecuritySolution {

    @Override
    public String startDecryption(byte[] data) {
        long[][] chopped = decode(data);
        long[] bigKey = new long[4];
        byte[] key = this.getKey();
        for (int i = 0; i < bigKey.length; i++) {
            int position = i * 4;
            bigKey[i] = bytesToLong(new byte[]{key[position],
                key[position + 1], key[position + 2], key[position + 3]});
            System.out.println(bigKey[i]);
        }
        for (int i = 0; i < chopped.length; i++) {
            decrypt(chopped[i], bigKey);
        }

        // TODO: DECODE TEA
        return unChopString(chopped);
    }

    @Override
    public byte[] startEncryption(String data) {
        long[][] chopped = chopString(data);
        long[] bigKey = new long[4];
        byte[] key = this.getKey();
        for (int i = 0; i < bigKey.length; i++) {
            int position = i * 4;
            bigKey[i] = bytesToLong(new byte[]{key[position],
                key[position + 1], key[position + 2], key[position + 3]});
            System.out.println(bigKey[i]);
        }
        for (int i = 0; i < chopped.length; i++) {
            encrypt(chopped[i], bigKey);
        }

        return longPairsToBytes(chopped);
    }

    public byte[] longPairsToBytes(long[][] bigs) {
        ArrayList<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < bigs.length; i++) {
            for (int j = 0; j < bigs[i].length; j++) {
                byte[] bigBytes = ByteBuffer.allocate(8).putLong(bigs[i][j]).array();
                for (int k = 0; k < bigBytes.length; k++) {
                    bytes.add(bigBytes[k]);
                }
            }
        }

        byte[] byteOutput = new byte[bytes.size()];
        int index = 0;
        for (Byte b : bytes) {
            byteOutput[index++] = b;
        }
        return byteOutput;
    }

//    public byte[] bigPairsToBytes(BigInteger[][] bigs) {
//        ArrayList<Byte> bytes = new ArrayList<>();
//        for (int i = 0; i < bigs.length; i++) {
//            for (int j = 0; j < bigs[i].length; j++) {
//                byte[] bigBytes = bigs[i][j].toByteArray();
//                for (int k = 0; k < bigBytes.length; k++) {
//                    bytes.add(bigBytes[k]);
//                }
//            }
//        }
//
//        byte[] byteOutput = new byte[bytes.size()];
//        int index = 0;
//        for (Byte b : bytes) {
//            byteOutput[index++] = b;
//        }
//        return byteOutput;
//    }
    public String unChopString(long[][] input) {
        String output = new String();
        for (long[] longPair : input) {
            for (long item : longPair) {
                //byte[] chars = item.toByteArray();
                byte[] chars = ByteBuffer.allocate(8).putLong(item).array();
                output = output + new String(chars);
            }
        }
        return output;
    }

    /*
    public void encrypt(BigInteger[] v, BigInteger[] k) {
        BigInteger v0 = v[0];
        BigInteger v1 = v[1];
        BigInteger sum = BigInteger.valueOf(0);
        BigInteger delta = BigInteger.valueOf(0x9e779b9);
        BigInteger k0 = k[0];
        BigInteger k1 = k[1];
        BigInteger k2 = k[2];
        BigInteger k3 = k[3];
        for (int i = 0; i < 32; i++) {
            sum = sum.add(delta);

            //adding v0
            BigInteger q1 = v1.shiftLeft(4);
            q1 = q1.add(k0);
            BigInteger q2 = v1.add(sum);
            BigInteger q3 = v1.shiftRight(5);
            q3 = q3.add(k1);
            v0 = v0.add(q1.xor(q2).xor(q3));

            q1 = v0.shiftLeft(4);
            q1 = q1.add(k2);
            q2 = v0.add(sum);
            q3 = v0.shiftRight(5);
            q3 = q3.add(k3);
            v1 = v1.add(q1.xor(q2).xor(q3));
        }
        v[0] = v0;
        v[1] = v1;
    }
     */
    public void encrypt(long[] v, long[] k) {
        long v0 = v[0];
        long v1 = v[1];
        long sum = 0;
        long delta = 0x9e779b9;
        long k0 = k[0];
        long k1 = k[1];
        long k2 = k[2];
        long k3 = k[3];
        for (int i = 0; i < 32; i++) {
            sum = sum + (delta);

            //adding v0
            long q1 = v1 << (4);
            q1 = q1 + (k0);
            long q2 = v1 + (sum);
            long q3 = v1 >> (5);
            q3 = q3 + (k1);
            v0 = v0 + ((q1) ^ (q2) ^ (q3));

            q1 = v0 << (4);
            q1 = q1 + (k2);
            q2 = v0 + (sum);
            q3 = v0 >> (5);
            q3 = q3 + (k3);
            v1 = v1 + ((q1) ^ (q2) ^ (q3));
        }
        v[0] = v0;
        v[1] = v1;
    }

    /*
    public void decrypt(BigInteger[] v, BigInteger[] k) {
        BigInteger v0 = v[0];
        BigInteger v1 = v[1];
        BigInteger delta = BigInteger.valueOf(0x9e779b9);
        BigInteger sum = delta.shiftLeft(5);
        BigInteger k0 = k[0];
        BigInteger k1 = k[1];
        BigInteger k2 = k[2];
        BigInteger k3 = k[3];

        for (int i = 0; i < 32; i++) {

            BigInteger q1 = v0.shiftLeft(4);
            q1 = q1.add(k2);
            BigInteger q2 = v0.add(sum);
            BigInteger q3 = v0.shiftRight(5);
            q3 = q3.add(k3);
            v1 = v1.subtract(q1.xor(q2).xor(q3));

            q1 = v1.shiftLeft(4);
            q1 = q1.add(k0);
            q2 = v1.add(sum);
            q3 = v1.shiftRight(5);
            q3 = q3.add(k1);
            v0 = v0.subtract(q1.xor(q2).xor(q3));

            sum = sum.subtract(delta);
        }
        v[0] = v0;
        v[1] = v1;
    }
     */
    public void decrypt(long[] v, long[] k) {
        long v0 = v[0];
        long v1 = v[1];
        long delta = 0x9e779b9;
        long sum = delta << 5;
        long k0 = k[0];
        long k1 = k[1];
        long k2 = k[2];
        long k3 = k[3];

        for (int i = 0; i < 32; i++) {

            long q1 = v0 << 4;
            q1 = q1 + k2;
            long q2 = v0 + (sum);
            long q3 = v0 >> (5);
            q3 = q3 + (k3);
            v1 = v1 - (q1 ^ (q2) ^ (q3));

            q1 = v1 << (4);
            q1 = q1 + (k0);
            q2 = v1 + (sum);
            q3 = v1 >> (5);
            q3 = q3 + (k1);
            v0 = v0 - (q1 ^ (q2) ^ (q3));

            sum = sum - (delta);
        }
        v[0] = v0;
        v[1] = v1;
    }

    //http://stackoverflow.com/questions/1026761/how-to-convert-a-byte-array-to-its-numeric-value-java
    public static long bytesToLong(byte[] data) {
        long value = 0;
        for (int i = 0; i < data.length; i++) {
            value = (value << 8) + (data[i] & 0xff);
        }
        return value;
    }

    public static long[][] decode(byte[] data) {
        ArrayList<long[]> list = new ArrayList<>();
        int remainder = data.length % 16;
        for (int i = 0; i < data.length - remainder; i += 16) {
            byte[] leftBytes = new byte[]{data[i],
                data[i + 1], data[i + 2],
                data[i + 3], data[i + 4],
                data[i + 5], data[i + 6],
                data[i + 7]};
            long left = bytesToLong(leftBytes);
            byte[] rightBytes = new byte[]{data[i + 8],
                data[i + 9], data[i + 10],
                data[i + 11], data[i + 12],
                data[i + 13], data[i + 14],
                data[i + 15]};
            long right = bytesToLong(rightBytes);
            list.add(new long[]{left, right});
        }

        int start = data.length - remainder;

        if (remainder != 0) {

            if (remainder > 8) {
                byte[] leftBytes = new byte[]{data[start],
                    data[start + 1], data[start + 2],
                    data[start + 3], data[start + 4],
                    data[start + 5], data[start + 6],
                    data[start + 7]};
                long left = bytesToLong(data);
                long right;
                start = start + 8;
                byte[] rightBytes = new byte[data.length - start];

                for (int i = 0; i < data.length - start; i++) {
                    rightBytes[i] = (byte) data[start + i];
                }

                right = bytesToLong(rightBytes);
                list.add(new long[]{left, right});
            } else {
                long left;
                byte[] leftBytes = new byte[data.length - start];
                for (int i = 0; i < data.length - start; i++) {
                    leftBytes[i] = (byte) data[start + i];
                }
                left = bytesToLong(leftBytes);
                list.add(new long[]{left, 0L});
            }
        }
        return list.toArray(new long[list.size()][]);
    }

    /*
    public static BigInteger[][] chop(byte[] data) {
        ArrayList<BigInteger[]> list = new ArrayList<>();
        int remainder = data.length % 8;
        for (int i = 0; i < data.length - remainder; i += 8) {
            byte[] leftBytes = new byte[]{data[i],
                data[i + 1], data[i + 2],
                data[i + 3]};
            BigInteger left = new BigInteger(leftBytes);

            byte[] rightBytes = new byte[]{data[i + 4],
                data[i + 5], data[i + 6],
                data[i + 7]};
            BigInteger right = new BigInteger(rightBytes);
            //System.out.println("" + data.charAt(i + 4) + data.charAt(i + 5) + data.charAt(i + 6) + data.charAt(i + 7));
            list.add(new BigInteger[]{left, right});
        }

        int start = data.length - remainder;

        if (remainder != 0) {

            if (remainder > 4) {
                byte[] leftBytes = new byte[]{data[start],
                    data[start + 1], data[start + 2],
                    data[start + 3]};
                BigInteger left = new BigInteger(leftBytes);
                BigInteger right;
                start = start + 4;
                byte[] rightBytes = new byte[data.length - start];

                for (int i = 0; i < data.length - start; i++) {
                    rightBytes[i] = (byte) data[start + i];
                }

                right = new BigInteger(rightBytes);
                list.add(new BigInteger[]{left, right});
            } else {
                BigInteger left;
                byte[] leftBytes = new byte[data.length - start];
                for (int i = 0; i < data.length - start; i++) {
                    leftBytes[i] = (byte) data[start + i];
                }
                left = new BigInteger(leftBytes);
                list.add(new BigInteger[]{left, BigInteger.ZERO});
            }
        }
        return list.toArray(new BigInteger[list.size()][]);
    }
     */
    public static long[][] chopString(String text) {

        byte[] data = text.getBytes();
        return decode(data);
    }

}
