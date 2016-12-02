/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textchatv2.algorithms;

import java.math.BigInteger;
import textchatv2.SecuritySolution;

/**
 *
 * @author lucas.burdell
 */
public class TEA extends SecuritySolution {

    @Override
    public String startDecryption(byte[] data) {

        // TODO: DECODE TEA
        return new String(data);
    }

    @Override
    public byte[] startEncryption(String data) {

        //byte[] encoded = new byte[data.length];
        // TODO: ENCODE TEA
        return data.getBytes();
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

}
