/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textchatv2;

/**
 *
 * @author lucas.burdell
 */
public abstract class SecuritySolution {

    private byte[] key;

    public abstract String startDecryption(byte[] data);

    public abstract byte[] startEncryption(String data);

    /**
     * @return the key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(byte[] key) {
        this.key = key;
    }
}
