/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textchatv2.algorithms;

import textchatv2.SecuritySolution;

/**
 *
 * @author lucas.burdell
 */
public class RC4 extends SecuritySolution {

    @Override
    public String startDecryption(byte[] data) {
        return new String(data);
    }

    @Override
    public byte[] startEncryption(String data) {
        return data.getBytes();
    }

}
