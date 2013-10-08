package ca.GabrielCastro.fanshaweconnect.util;

import android.util.Base64;

public class SecretKeyGenerator {

    /*package*/ static char[] getSecretKey() {
        return new String(
                Base64.decode("AAAAB3NzaC1yc2EAAAABJQAAAQEAxY/wxbgH6s8J1PzMa3upDvxOd13g7oo+Yski4" +
                        "t672WGGUV4vcFZuiR2znxOT4ebmZ7KfVE6kT6rHmMwsLlWxQ3oMTj8xsPHx7NR1Pt" +
                        "RCarLUztS8vONw5UoL6hSlrANKRd6pPL/Q9QD05z+9IWtsfs2e3FGEVv75L7Ibv1j" +
                        "1/H98lTotulqkulmQD3qd/iZIoLHI6qMPpZaG79++Mg+9WVQIKUZQ/+nMSdojpJaj" +
                        "WqOMhj4Vltk1hBr/sQQh5mEteNL3rkX8P3+lKEyq7FlPSwb67yeYs6mkgBwVNy+u6" +
                        "ht84DKmoDl60B+oXs/cqQaWu8Bvu+tTIQmt0x8SRB7jLQ==",
                        Base64.DEFAULT)
        ).toCharArray();
    }

}
