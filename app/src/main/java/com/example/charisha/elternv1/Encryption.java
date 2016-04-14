package com.example.charisha.elternv1;

/**
 * Created by e-Miracle workers.
 * Class to hash the password entered by the user
 */
public class Encryption {

    public String hashPassword(String password) {
        try {
            return byteArrayToHexString(Encryption.computeHash(password));
        }
        catch (Exception e){
            e.printStackTrace();
            return "Fail to hash";
        }
    }

    public static byte[] computeHash(String x) {
        try {
            java.security.MessageDigest d;
            d = java.security.MessageDigest.getInstance("SHA-1");
            d.reset();
            d.update(x.getBytes());
            return  d.digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byteArrayToHexString(byte[] bytePassword){
        StringBuilder stringBuilder = new StringBuilder(bytePassword.length * 2);
        for (int i = 0; i < bytePassword.length; i++){
            int element = bytePassword[i] & 0xff;
            if (element < 16) {
                stringBuilder.append('0');
            }
            stringBuilder.append(Integer.toHexString(element));
        }
        return stringBuilder.toString().toUpperCase();
    }
}
