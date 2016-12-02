/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textchatv2.algorithms;

import java.math.BigInteger;
import java.util.ArrayList;
import textchatv2.SecuritySolution;

/**
 *
 * @author lucas.burdell
 */
public class TEA extends SecuritySolution {

    @Override
    public String startDecryption(byte[] data) {
        BigInteger[][] chopped = chopString(new String(data));
        BigInteger[] bigKey = new BigInteger[4];
        byte[] key = this.getKey();
        for (int i = 0; i < bigKey.length; i++) {
            int position = i * 4;
            bigKey[i] = new BigInteger(new byte[]{key[position],
                key[position + 1], key[position + 2], key[position + 3]});
        }
        for (int i = 0; i < chopped.length; i++) {
            decrypt(chopped[i], bigKey);
        }

        // TODO: DECODE TEA
        return unChopString(chopped);
    }

    @Override
    public byte[] startEncryption(String data) {
        BigInteger[][] chopped = chopString(data);
        BigInteger[] bigKey = new BigInteger[4];
        byte[] key = this.getKey();
        for (int i = 0; i < bigKey.length; i++) {
            int position = i * 4;
            bigKey[i] = new BigInteger(new byte[]{key[position],
                key[position + 1], key[position + 2], key[position + 3]});
        }
        for (int i = 0; i < chopped.length; i++) {
            encrypt(chopped[i], bigKey);
        }

        return unChopString(chopped).getBytes();
    }

    public String unChopString(BigInteger[][] input) {
        String output = new String();
        for (BigInteger[] integerPair : input) {
            for (BigInteger item : integerPair) {
                byte[] chars = item.toByteArray();
                output = output + new String(chars);
            }
        }
        return output;
    }

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

    public static BigInteger[][] chopString(String text) {

        ArrayList<BigInteger[]> list = new ArrayList<>();
        int remainder = text.length() % 8;
        for (int i = 0; i < text.length() - remainder; i += 8) {
            byte[] leftBytes = new byte[]{(byte) text.charAt(i),
                (byte) text.charAt(i + 1), (byte) text.charAt(i + 2),
                (byte) text.charAt(i + 3)};
            BigInteger left = new BigInteger(leftBytes);

            byte[] rightBytes = new byte[]{(byte) text.charAt(i + 4),
                (byte) text.charAt(i + 5), (byte) text.charAt(i + 6),
                (byte) text.charAt(i + 7)};
            BigInteger right = new BigInteger(rightBytes);
            //System.out.println("" + text.charAt(i + 4) + text.charAt(i + 5) + text.charAt(i + 6) + text.charAt(i + 7));
            list.add(new BigInteger[]{left, right});
        }

        int start = text.length() - remainder;

        if (remainder != 0) {

            if (remainder > 4) {
                byte[] leftBytes = new byte[]{(byte) text.charAt(start),
                    (byte) text.charAt(start + 1), (byte) text.charAt(start + 2),
                    (byte) text.charAt(start + 3)};
                BigInteger left = new BigInteger(leftBytes);
                BigInteger right;
                start = start + 4;
                byte[] rightBytes = new byte[text.length() - start];

                for (int i = 0; i < text.length() - start; i++) {
                    rightBytes[i] = (byte) text.charAt(start + i);
                }

                right = new BigInteger(rightBytes);
                list.add(new BigInteger[]{left, right});
            } else {
                BigInteger left;
                byte[] leftBytes = new byte[text.length() - start];
                for (int i = 0; i < text.length() - start; i++) {
                    leftBytes[i] = (byte) text.charAt(start + i);
                }
                left = new BigInteger(leftBytes);
                list.add(new BigInteger[]{left, BigInteger.ZERO});
            }
        }
        return list.toArray(new BigInteger[list.size()][]);
    }

}
