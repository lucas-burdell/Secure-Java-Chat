package textchatv2.algorithms;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import textchatv2.SecuritySolution;

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
        for (long[] longs : chopped) {
            encrypt(longs, bigKey);
        }

        return longPairsToBytes(chopped);
    }

    public byte[] longPairsToBytes(long[][] bigs) {
        ArrayList<Byte> bytes = new ArrayList<>();
        for (long[] big : bigs) {
            for (int j = 0; j < big.length; j++) {
                byte[] bigBytes = ByteBuffer.allocate(8).putLong(big[j]).array();
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

    public static long[][] chopString(String text) {
        byte[] data = text.getBytes();
        return decode(data);
    }

}
