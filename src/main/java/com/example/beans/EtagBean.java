package com.example.beans;
import java.util.Base64;

import org.springframework.stereotype.Service;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EtagBean {
    public String generateEtag(String str) throws NoSuchAlgorithmException {
        // MessageDigest md = MessageDigest.getInstance("MD5");
        // md.update(str.getBytes());
        // byte[] digest = md.digest();
        // String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(str.getBytes());
        String myHash = Base64.getEncoder().encodeToString(hash);
        return myHash;
    }
}