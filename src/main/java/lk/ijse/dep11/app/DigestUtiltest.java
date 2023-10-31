package lk.ijse.dep11.app;

import org.apache.commons.codec.digest.DigestUtils;

public class DigestUtiltest {
    public static void main(String[] args) {
        System.out.println(DigestUtils.sha256Hex("1234"));
    }
}
