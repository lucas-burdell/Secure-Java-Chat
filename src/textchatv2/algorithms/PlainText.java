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
public class PlainText extends SecuritySolution {

    @Override
    public String startDecryption(String data) {
        return data;
    }

    @Override
    public String startEncryption(String data) {
        return data;
    }

}
