package textchatv2;

public abstract class SecuritySolution {

    private byte[] key;

    public abstract String startDecryption(byte[] data);

    public abstract byte[] startEncryption(String data);

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
