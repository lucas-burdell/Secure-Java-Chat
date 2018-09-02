package textchatv2.algorithms;

import textchatv2.SecuritySolution;

public class PlainText extends SecuritySolution {

    @Override
    public String startDecryption(byte[] data) {
        return new String(data);
    }

    @Override
    public byte[] startEncryption(String data) {
        return data.getBytes();
    }
}
