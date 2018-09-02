package textchatv2.algorithms;

import textchatv2.SecuritySolution;

public class RC4 extends SecuritySolution {

    private byte[] S = new byte[256];
    private byte[] K = new byte[256];
    private int N;
    private int i;
    private int j;

    @Override
    public void setKey(byte[] key) {
        super.setKey(key);
        N = key.length;
        for (i = 0; i < 256; i++) {
            S[i] = (byte) i;
            K[i] = key[intMod(i, N)];

        }
        j = 0;
        byte swap;
        for (i = 0; i < 256; i++) {
            j = intMod((j + (S[i]) + (K[i])), 256);
            swap(S, i, j);
        }
        i = j = 0;
    }

    public int intMod(int i, int mod) {
        int out = i % mod;
        if (out < 0) {
            out += mod;
        }
        return out;
    }

    @Override
    public String startDecryption(byte[] data) {
        return decrypt(data);
    }

    @Override
    public byte[] startEncryption(String data) {
        return encrypt(data);
    }

    public void swap(byte[] array, int pos1, int pos2) {
        byte temp = array[pos1];
        array[pos1] = array[pos2];
        array[pos2] = temp;
    }

    public byte getKeyStreamByte() {
        i = intMod((i + 1), 256);
        j = intMod((j + (S[i])), 256);
        swap(S, i, j);
        int t = intMod(((S[i]) + (S[j])), 256);
        return S[t];
    }

    public byte[] encrypt(String plainText) {
        byte[] input = plainText.getBytes();
        byte[] output = new byte[input.length];
        byte keyStreamByte = getKeyStreamByte();
        for (int k = 0; k < input.length; k++) {
            output[k] = (byte) (input[k] ^ keyStreamByte);
        }
        return output;
    }

    public String decrypt(byte[] cipherText) {
        byte[] input = cipherText;
        byte[] output = new byte[input.length];
        byte keyStreamByte = getKeyStreamByte();
        for (int k = 0; k < input.length; k++) {
            output[k] = (byte) (input[k] ^ keyStreamByte);
        }
        return new String(output);
    }
}