import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

public class KeyGenerator {

    KeyPair key;
    public KeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ec = new ECGenParameterSpec("secp256r1");
        g.initialize(ec);
        key = g.genKeyPair();

        PublicKey pubkey = key.getPublic();

        ECPrivateKey ecPrivKey = (ECPrivateKey) key.getPrivate();
        ECPublicKey ecPubKey = (ECPublicKey) key.getPublic();
        String privAsHex = ecPrivKey.getS().toString(16);

        /*
        System.out.println("");
        System.out.println("Private key: " + privAsHex);

        System.out.println("");
        System.out.println("Public key: " + ecPubKey);

         */
    }

    public KeyPair getKey() {
        return key;
    }
}
