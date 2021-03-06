import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.InvalidAlgorithmParameterException;

/**
 * A simple class to generate a set of RSA (public and private) keys and write
 * those keys to files for exchange with other environments. Four files will be
 * generated containing the public and private keys in byte and text(hex)
 * formats.
 */
public class GenerateRSAKeys {
	public GenerateRSAKeys() {
	}

	public static void main(String[] args) throws java.io.IOException {
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			// The 512 is the key size. For better encryption increase to 2048
			keyGen.initialize(new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4));
		} catch (NoSuchAlgorithmException noAlgorithm) {
			System.out.println("No RSA provider available!");
			return;
		} catch (InvalidAlgorithmParameterException invalidAlgorithm) {
			System.out.println("Invalid algorithm for RSA!");
			return;
		}

		KeyPair keyPair = keyGen.generateKeyPair();

		RSAPrivateKey secretKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// Write out the ASN.1 and raw key files
		FileOutputStream fOut = new FileOutputStream("src/temp/private_asn1_key");
		fOut.write(secretKey.getEncoded());
		fOut.close();

		fOut = new FileOutputStream("src/temp/public_asn1_key");
		fOut.write(publicKey.getEncoded());
		fOut.close();

		FileWriter fw = new FileWriter("src/temp/private_raw.txt");
		fw.write(secretKey.getModulus().toString(16).toUpperCase());
		fw.write("\n");
		fw.write(secretKey.getPrivateExponent().toString(16).toUpperCase());
		fw.close();

		fw = new FileWriter("src/temp/public_raw.txt");
		fw.write(publicKey.getModulus().toString(16).toUpperCase());
		fw.write("\n");
		fw.write(publicKey.getPublicExponent().toString(16).toUpperCase());
		fw.close();

		System.out.println("RSA keys generated successfully.");
	}
}