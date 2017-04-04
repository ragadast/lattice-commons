/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 1, 2008
 */
package org.latticesoft.util.common;

import java.util.*;
import java.io.*;
import java.security.*;
import java.math.*;
import javax.crypto.*;

import java.security.cert.Certificate;
import java.security.spec.*;
import javax.crypto.spec.*;
import java.security.cert.*;

import org.bouncycastle.x509.X509V3CertificateGenerator;
//import org.bouncycastle.x509.*;
import org.bouncycastle.jce.*;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.util.container.*;

public final class CryptoUtil {
	private static final Log log = LogFactory.getLog(CryptoUtil.class);
	public static KeyPair generateKeyPair() {
		return generateKeyPair("RSA", 1024);
	}
	public static KeyPair generateKeyPair(String algorithm, int strength) {
		KeyPair kp = null;
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance(algorithm);
			kpg.initialize(strength, CryptoUtil.getSecureRandom());
		    kp = kpg.generateKeyPair();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in generating the Key Pair", e);
			}
		}
		return kp;
	}
	
	public static Key generateKey(String algorithm, int strength) {
		Key k = null;
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(algorithm);
			kg.init(strength, CryptoUtil.getSecureRandom());
		    k = kg.generateKey();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in generating the Key Pair", e);
			}
		}
		return k;
	}

	public static SecureRandom getSecureRandom(String algorithm, String provider) {
		SecureRandom sr = null;
		try {
			sr = SecureRandom.getInstance(algorithm, provider);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception in getting secure random", e);
			}
		}
		
		return sr;
	}

	public static SecureRandom getSecureRandom() {
		return getSecureRandom("SHA1PRNG", "SUN");
	}
	public static boolean isKeyPair(Map map) {
		PropertyMap pm = null;
		if (map instanceof PropertyMap) {
			pm = (PropertyMap)map;
		} else {
			pm = new PropertyMap();
			pm.putAll(map);
		}
		String algor = pm.getString("algorithm");
		boolean retVal = false;
		if ("RSA".equalsIgnoreCase(algor) || "DSA".equalsIgnoreCase(algor)) {
			retVal = true;
		}
		return retVal;
	}
	
	public static boolean isKeyPair(String s) {
		Map map = StringUtil.mapFromString(s);
		return isKeyPair(map);
	}
	
	public static final String WRAP_CRYPTO = "crypto";
	public static final String WRAP_ALGORITHM = "algorithm";
	public static final String WRAP_STRENGTH = "strength";
	public static final String WRAP_KEY = "key";
	public static final String WRAP_KEYPAIR = "keyPair";
	public static final String WRAP_PUBLIC_KEY = "publicKey";
	public static final String WRAP_PRIVATE_KEY = "privateKey";
	public static final String WRAP_CIPHER_MODE = "cipherMode";
	public static final String WRAP_CIPHER_PADDING = "cipherPadding";
	
	public static final String WRAP_KEY_ENCODED = "keyEncoded";
	public static final String WRAP_PUBLIC_KEY_ENCODED = "publicKeyEncoded";
	public static final String WRAP_PRIVATE_KEY_ENCODED = "privateKeyEncoded";
	
	public static Map unwrap(String s) {
		Map map = StringUtil.mapFromString(s);
		map.put(CryptoUtil.WRAP_CRYPTO, s);
		PropertyMap pm = new PropertyMap();
		pm.putAll(map);
		String algor = pm.getString(CryptoUtil.WRAP_ALGORITHM);
		if (log.isDebugEnabled()) {
			log.debug("algorithm: " + algor);
		}
		//int strength = pm.getInt("strength");
		if (algor == null) {
			return null;
		}
		if ("RSA".equalsIgnoreCase(algor) || "DSA".equalsIgnoreCase(algor)) {
			KeyPair kp = null;
			String pubKeyEnc = pm.getString(CryptoUtil.WRAP_PUBLIC_KEY_ENCODED);
			String priKeyEnc = pm.getString(CryptoUtil.WRAP_PRIVATE_KEY_ENCODED);
			try {
				byte[] pubKeyEncoded = ConvertUtil.convertStringToByteArray(pubKeyEnc);
				X509EncodedKeySpec keySpec509 = new X509EncodedKeySpec(pubKeyEncoded);
				PublicKey pubKey = KeyFactory.getInstance(algor).generatePublic(keySpec509);
			
				byte[] priKeyEncoded = ConvertUtil.convertStringToByteArray(priKeyEnc);
				PKCS8EncodedKeySpec keySpecPkcs = new PKCS8EncodedKeySpec(priKeyEncoded);
				PrivateKey priKey = KeyFactory.getInstance(algor).generatePrivate(keySpecPkcs);
				pm.put(CryptoUtil.WRAP_PUBLIC_KEY, pubKey);
				pm.put(CryptoUtil.WRAP_PRIVATE_KEY, priKey);
				kp = new KeyPair(pubKey, priKey);
				pm.put(CryptoUtil.WRAP_KEYPAIR, kp);
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error("Exception", e);
				}
			}
		} else if ("DES".equalsIgnoreCase(algor) ||
				"DESede".equalsIgnoreCase(algor) ||
				"AES".equalsIgnoreCase(algor) ||
				"Blowfish".equalsIgnoreCase(algor)){
			String keyStr = pm.getString(CryptoUtil.WRAP_KEY_ENCODED);
			byte[] keyEncoded = ConvertUtil.convertStringToByteArray(keyStr);
			if (log.isInfoEnabled()) {
				log.info(keyStr.length() + ":" + keyEncoded.length);
			}
			Key key = new SecretKeySpec(keyEncoded, algor);
			pm.put(CryptoUtil.WRAP_KEY, key);
		}
		return pm;
	}
	
	public static String unwrapAlgorithm(Map map) {
		String retVal = null;
		if (map != null && map.containsKey(CryptoUtil.WRAP_ALGORITHM)) {
			retVal = (String)map.get(CryptoUtil.WRAP_ALGORITHM);
		}
		return retVal;
	}
	public static String unwrapAlgorithm(String s) {
		Map map = CryptoUtil.unwrap(s);
		return unwrapAlgorithm(map);
	}
	public static KeyPair unwrapKeyPair(Map map) {
		KeyPair kp = null;
		if (map != null && map.containsKey(CryptoUtil.WRAP_KEYPAIR)) {
			kp = (KeyPair)map.get(CryptoUtil.WRAP_KEYPAIR);
		}
		return kp;
	}
	public static KeyPair unwrapKeyPair(String s) {
		Map map = CryptoUtil.unwrap(s);
		return CryptoUtil.unwrapKeyPair(map);
	}
	public static Key unwrapKey(Map map) {
		Key key = null;
		if (map != null && map.containsKey(CryptoUtil.WRAP_KEY)) {
			key = (Key)map.get(CryptoUtil.WRAP_KEY);
		}
		return key;
	}
	public static Key unwrapKey(String s) {
		Map map = CryptoUtil.unwrap(s);
		return CryptoUtil.unwrapKey(map);
	}

	public static String unwrapCipherMode(Map map) {
		String retVal = null;
		if (map != null && map.containsKey(CryptoUtil.WRAP_CIPHER_MODE)) {
			retVal = (String)map.get(CryptoUtil.WRAP_CIPHER_MODE);
		}
		return retVal;
	}
	public static String unwrapCipherMode(String s) {
		Map map = CryptoUtil.unwrap(s);
		return unwrapCipherMode(map);
	}
	public static String unwrapCipherPadding(Map map) {
		String retVal = null;
		if (map != null && map.containsKey(CryptoUtil.WRAP_CIPHER_PADDING)) {
			retVal = (String)map.get(CryptoUtil.WRAP_CIPHER_PADDING);
		}
		return retVal;
	}
	public static String unwrapCipherPadding(String s) {
		Map map = CryptoUtil.unwrap(s);
		return unwrapCipherMode(map);
	}

	
	public static String wrap(KeyPair kp, String algorithm) {
		return wrap(kp, algorithm, 0);
	}
	public static String wrap(KeyPair kp, String algorithm, int strength) {
		if (kp == null || algorithm == null) {
			return null;
		}
		return wrap(kp, algorithm, strength, null, null);
	}
	public static String wrap(KeyPair kp, String algorithm, int strength, String cipherMode, String cipherPadding) {
		if (kp == null || algorithm == null) {
			return null;
		}
		Map map = new HashMap();
		PublicKey pubKey = kp.getPublic();
		PrivateKey priKey = kp.getPrivate();
		String pubKeyStr = ConvertUtil.convertByteArrayToString(pubKey.getEncoded());
		String priKeyStr = ConvertUtil.convertByteArrayToString(priKey.getEncoded());
		map.put(CryptoUtil.WRAP_ALGORITHM, algorithm);
		map.put(CryptoUtil.WRAP_PUBLIC_KEY_ENCODED, pubKeyStr);
		map.put(CryptoUtil.WRAP_PRIVATE_KEY_ENCODED, priKeyStr);
		map.put(CryptoUtil.WRAP_STRENGTH, "" + strength);
		if (cipherMode != null && cipherPadding != null) {
			map.put(CryptoUtil.WRAP_CIPHER_MODE, "" + cipherMode);
			map.put(CryptoUtil.WRAP_CIPHER_PADDING, "" + cipherPadding);
		}
		return map.toString();
	}

	public static String wrap(Key key, String algorithm) {
		return wrap(key, algorithm, 0);
	}
	public static String wrap(Key key, String algorithm, int strength) {
		if (key == null || algorithm == null) {
			return null;
		}
		return wrap(key, algorithm, strength, null, null);
	}
	public static String wrap(Key key, String algorithm, int strength, String cipherMode, String cipherPadding) {
		if (key == null || algorithm == null) {
			return null;
		}
		
		Map map = new HashMap();
		String keyStr = ConvertUtil.convertByteArrayToString(key.getEncoded());
		map.put(CryptoUtil.WRAP_ALGORITHM, algorithm);
		map.put(CryptoUtil.WRAP_KEY_ENCODED, keyStr);
		map.put(CryptoUtil.WRAP_STRENGTH, "" + strength);
		if (cipherMode != null && cipherPadding != null) {
			map.put(CryptoUtil.WRAP_CIPHER_MODE, "" + cipherMode);
			map.put(CryptoUtil.WRAP_CIPHER_PADDING, "" + cipherPadding);
		}
		return map.toString();
	}
	
	
	public static String convertKeyToString(Key key) {
		return ConvertUtil.convertByteArrayToString(key.getEncoded());
	}
	
	public static String decrypt(Map map, String data, boolean isHexString) {
		if (map == null || data == null || 
			!map.containsKey(CryptoUtil.WRAP_ALGORITHM) || 
			!(map.containsKey(CryptoUtil.WRAP_KEY) || 
			map.containsKey(CryptoUtil.WRAP_KEYPAIR))) {
			return null;
		}
			
		String retVal = null;
		byte[] b = null;
		Cipher cipher = null;
		try {
			String algorithm = CryptoUtil.unwrapAlgorithm(map);
			String cipherMode = CryptoUtil.unwrapCipherMode(map);
			String cipherPadding = CryptoUtil.unwrapCipherPadding(map);
			
			if (isHexString) {
				b = NumeralUtil.convertHexStringToByteArray(data);
			} else {
				b = data.getBytes(); 
			}
			StringBuffer sb = new StringBuffer();
			sb.append(algorithm);
			if (cipherMode != null && cipherPadding != null) {
				sb.append("/").append(cipherMode);
				sb.append("/").append(cipherPadding);
			}
			if (log.isDebugEnabled()) {
				log.debug("Cipher Instance: " + sb.toString());
			}
			cipher = Cipher.getInstance(sb.toString());
			if (CryptoUtil.isKeyPair(map)) {
				KeyPair kp = CryptoUtil.unwrapKeyPair(map);
				cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
			} else {
				Key key = CryptoUtil.unwrapKey(map);
				if (log.isInfoEnabled()) {
					log.info("key: " + key);
				}
				cipher.init(Cipher.DECRYPT_MODE, key);
			}
			byte[] result = cipher.doFinal(b);
			retVal = new String(result);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in crypto", e);
			}
		}
		return retVal;
	}
	
	public static String encrypt(Map map, String data, boolean toHexString) {
		if (map == null || data == null || 
			!map.containsKey(CryptoUtil.WRAP_ALGORITHM) || 
			!(map.containsKey(CryptoUtil.WRAP_KEY) || 
			map.containsKey(CryptoUtil.WRAP_KEYPAIR))) {
			return null;
		}
		String retVal = null;
		Cipher cipher = null;
		byte[] b = null;
		byte[] result = null;
		try {
			String algorithm = CryptoUtil.unwrapAlgorithm(map);
			String cipherMode = CryptoUtil.unwrapCipherMode(map);
			String cipherPadding = CryptoUtil.unwrapCipherPadding(map);

			StringBuffer sb = new StringBuffer();
			sb.append(algorithm);
			if (cipherMode != null && cipherPadding != null) {
				sb.append("/").append(cipherMode);
				sb.append("/").append(cipherPadding);
			}
			if (log.isDebugEnabled()) {
				log.debug("Cipher Instance: " + sb.toString());
			}
			cipher = Cipher.getInstance(sb.toString());

			if (CryptoUtil.isKeyPair(map)) {
				KeyPair kp = CryptoUtil.unwrapKeyPair(map);
				cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
			} else {
				Key key = CryptoUtil.unwrapKey(map);
				cipher.init(Cipher.ENCRYPT_MODE, key);
			}
			b = data.getBytes();
			result = cipher.doFinal(b);
			if (toHexString) {
				retVal = NumeralUtil.toHexString(result);
			} else {
				retVal = new String(result);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error", e);
			}
		}
		return retVal;
	}

	
	public static String decrypt(String keyEncoded, String data) {
		return decrypt(keyEncoded, data, true);
	}
	public static String decrypt(String keyEncoded, String data, boolean isHexString) {
		Map map = CryptoUtil.unwrap(keyEncoded);
		return decrypt(map, data, isHexString);
	}
	public static String encrypt(String keyEncoded, String data) {
		return encrypt(keyEncoded, data, true);
	}
	public static String encrypt(String keyEncoded, String data, boolean toHexString) {
		Map map = CryptoUtil.unwrap(keyEncoded);
		return encrypt(map, data, toHexString);
	}
	
	
	public static void addProvider(Provider p) {
		if (p != null) {
			Security.addProvider(p);
		}
	}
	
	public static void loadKeyStore(KeyStore ks, String location, String pwd) {
		if (ks == null || pwd == null || location == null) {
			return;
		}
		InputStream is = null;
		try {
			File f = new File(location);
			is = new FileInputStream(f);
			char[] pwdBytes = pwd.toCharArray();
			ks.load(is, pwdBytes);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in loading keystore", e);
			}
		} finally {
			try { is.close(); } catch (Exception e) {}
			is = null;
		}
	}
	public static KeyStore getKeyStore(String location, String type, String pwd) {
		return CryptoUtil.getKeyStore(location, type, null, pwd);
	}
	
	public static KeyStore getKeyStore(String location, String type, String provider, String pwd) {
		KeyStore ks = null;
		try {
			if (provider == null) {
				ks = KeyStore.getInstance(type);
			} else {
				ks = KeyStore.getInstance(type, provider);
			}
			CryptoUtil.loadKeyStore(ks, location, pwd);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in initialising keystore", e);
			}
		}
		return ks;
	}
	
	public static Certificate generateCertFromKeyPair(KeyPair kp, String algor) {
		Hashtable subject = new Hashtable();
		subject.put(X509Principal.CN, "CryptoUtil");
		subject.put(X509Principal.O, "LatticeSoft Org");
		subject.put(X509Principal.C, "sg");


		Hashtable issuer = new Hashtable();
		issuer.put(X509Principal.CN, "CryptoUtil");
		issuer.put(X509Principal.O, "LatticeSoft Org");
		issuer.put(X509Principal.C, "sg");

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		certGen.setSerialNumber(BigInteger.valueOf(Math.abs(NumeralUtil.getRandomLong())));
		certGen.setIssuerDN(new X509Principal(issuer));
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 10 * 60 * 1000));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + (60 * (24 * 60 * 60 * 1000))));
		certGen.setSubjectDN(new X509Principal(subject));
		certGen.setPublicKey(kp.getPublic());
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		//certGen.setSignatureAlgorithm("RSA");
		X509Certificate cert = null;
		try {
			cert = certGen.generate(kp.getPrivate());
			cert.checkValidity(new Date());
			cert.verify(kp.getPublic());
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Erro in generating cert", e);
			}
		} 
		return cert;
	}
	
	public static Certificate[] generateCertChainFromKeyPair(KeyPair kp, String algor) {
		Certificate[] c = null;
		return c;
	}
	
	
	public static void setKeyPairInKeyStore(KeyPair kp, String algorithm, String alias, KeyStore ks, String pwd) {
		if (kp == null || ks == null || alias == null || pwd == null) {
			return;
		}
		try {
			PublicKey pubKey = kp.getPublic();
			PrivateKey priKey = kp.getPrivate();
			KeyStore.PasswordProtection kspwdprot = new KeyStore.PasswordProtection(pwd.toCharArray());
			
			//CertificateFactory certFac = CertificateFactory.getInstance("X.509");
			Certificate cert = CryptoUtil.generateCertFromKeyPair(kp, algorithm);
			Certificate[] chain = {cert};
			
			//chain = certFac.generateCertificates(is).toArray(chain);
			KeyStore.PrivateKeyEntry pkEntry = new KeyStore.PrivateKeyEntry(priKey, chain);
			EncryptedPrivateKeyInfo epki = new EncryptedPrivateKeyInfo(pubKey.getEncoded());
			
			ks.setKeyEntry(alias, epki.getEncoded(), chain);
			ks.setEntry(alias + "priKey", pkEntry, kspwdprot);
			
			pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(alias + "prikey", kspwdprot);
			priKey = pkEntry.getPrivateKey();
			kp = new KeyPair(pubKey, priKey);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in setting keypair into keystore", e);
			}
		}
	}
	
	public static KeyPair getKeyPairFromKeyStore(String alias, KeyStore ks, String pwd) {
		KeyPair kp = null;
		if (ks == null || alias == null || pwd == null) {
			return kp;
		}
		try {
			PublicKey pubKey = null;
			PrivateKey priKey = null;
			KeyStore.PasswordProtection kspwdprot = new KeyStore.PasswordProtection(pwd.toCharArray());
			KeyStore.PrivateKeyEntry pkEntry = null;
			if (ks.isKeyEntry(alias)) {
				pubKey = (PublicKey)ks.getKey(alias, pwd.toCharArray());
			} else if (ks.isCertificateEntry(alias)) {
				Certificate cert = ks.getCertificate(alias);
				pubKey = cert.getPublicKey();
			}
			pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(alias + "prikey", kspwdprot);
			priKey = pkEntry.getPrivateKey();
			kp = new KeyPair(pubKey, priKey);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in getting keypair from keystore", e);
			}
		}
		return kp;
	}
	
	
	public static void test() {
		try {
			/*
			//KeyGenerator kg = KeyGenerator.getInstance("RSA");
			//Key key = kg.generateKey();
			KeyPair kp = CryptoUtil.generateKeyPair();
			Key key = null;
			Cipher cipher = Cipher.getInstance("RSA");
			
			byte[] data = "The quick brown fox jumps over the lazy dog.".getBytes();
			System.out.println("Original data : [" + new String(data) + "]");

			key = kp.getPublic();
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(data);
			String s = NumeralUtil.toHexString(result);
			byte[] result2 = NumeralUtil.convertHexStringToByteArray(s);
			System.out.println("Encrypted data: [" + s + "]");
			System.out.println("Encrypted data: [" + new String(result2) + "]");

			key = kp.getPrivate();
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] original = cipher.doFinal(result);
			System.out.println("Decrypted data: [" + new String(original) + "]");
			byte[] original2 = cipher.doFinal(result2);
			System.out.println("Decrypted data: [" + new String(original2) + "]");
			//*/
			
			
			//String[] algorP = {"RSA", "DSA",  "DiffieHellman"};
			String[] algorP = {"RSA"};
			byte[] data = "The quick brown fox jumps over the lazy dog.".getBytes();
		
			for (int i=0; i<algorP.length; i++) {
			
				System.out.println("===== " + algorP[i] + " =====");
				KeyPairGenerator kg = KeyPairGenerator.getInstance(algorP[i]);
				kg.initialize(1024, CryptoUtil.getSecureRandom());
				KeyPair kp = kg.generateKeyPair();
			
				PublicKey pubKey1 = kp.getPublic();
				X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(pubKey1.getEncoded());
				PublicKey pubKey2 = KeyFactory.getInstance(algorP[i]).generatePublic(keySpec1);
				
				PrivateKey priKey1 = kp.getPrivate();
				PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(priKey1.getEncoded());
				PrivateKey priKey2 = KeyFactory.getInstance(algorP[i]).generatePrivate(keySpec2);
				
				String s1 = ConvertUtil.convertByteArrayToString(pubKey1.getEncoded());
				String s2 = ConvertUtil.convertByteArrayToString(pubKey2.getEncoded());
				System.out.println(s1);
				System.out.println(s2);
				System.out.println(s1.equals(s2));

				s1 = ConvertUtil.convertByteArrayToString(priKey1.getEncoded());
				s2 = ConvertUtil.convertByteArrayToString(priKey2.getEncoded());
				System.out.println(s1);
				System.out.println(s2);
				System.out.println(s1.equals(s2));
				
				System.out.println("==========");
				Cipher cipher = Cipher.getInstance(algorP[i]);
				System.out.println("Original data : [" + new String(data) + "]");
				cipher.init(Cipher.ENCRYPT_MODE, pubKey1);
				byte[] result = cipher.doFinal(data);
				String s = NumeralUtil.toHexString(result);
				byte[] result2 = NumeralUtil.convertHexStringToByteArray(s);
				System.out.println("Encrypted data: [" + s + "]");
				System.out.println("Encrypted data: [" + new String(result2) + "]");

				cipher.init(Cipher.DECRYPT_MODE, priKey2);
				byte[] original = cipher.doFinal(result);
				System.out.println("Decrypted data: [" + new String(original) + "]");
				byte[] original2 = cipher.doFinal(result2);
				System.out.println("Decrypted data: [" + new String(original2) + "]");
				System.out.println("==================================================");

			}
			
			String[] algor = {"DES", "DESede", "AES", "Blowfish"};
			boolean[] b = {false, false, false, false};
			
			
			for (int i=0; i<algor.length; i++) {
				System.out.println("===== " + algor[i] + " =====");
				KeyGenerator kg = KeyGenerator.getInstance(algor[i]);
				kg.init(128, CryptoUtil.getSecureRandom());

				Key key1 = kg.generateKey();
				Key ks = new SecretKeySpec(key1.getEncoded(), algor[i]);;
				Key key2 = null;
				if (b[i]) {
					key2 = SecretKeyFactory.getInstance(algor[i]).generateSecret((KeySpec)ks);
				} else {
					key2 = new SecretKeySpec(key1.getEncoded(), algor[i]);
				}
				String s1 = ConvertUtil.convertByteArrayToString(key1.getEncoded());
				String s2 = ConvertUtil.convertByteArrayToString(key2.getEncoded());
				System.out.println(s1);
				System.out.println(s2);
				System.out.println(s1.equals(s2));
				
				System.out.println("==========");
				Cipher cipher = Cipher.getInstance(algor[i]);
				System.out.println("Original data : [" + new String(data) + "]");
				cipher.init(Cipher.ENCRYPT_MODE, key1);
				byte[] result = cipher.doFinal(data);
				String s = NumeralUtil.toHexString(result);
				byte[] result2 = NumeralUtil.convertHexStringToByteArray(s);
				System.out.println("Encrypted data: [" + s + "]");
				System.out.println("Encrypted data: [" + new String(result2) + "]");

				cipher.init(Cipher.DECRYPT_MODE, key2);
				byte[] original = cipher.doFinal(result);
				System.out.println("Decrypted data: [" + new String(original) + "]");
				byte[] original2 = cipher.doFinal(result2);
				System.out.println("Decrypted data: [" + new String(original2) + "]");
				System.out.println("==================================================");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final String KEYSTORE_TYPE_JKS = "JKS";
	public static final String KEYSTORE_PROVIDER_SUN = "SUN";
	
	public static void testKeyStore() {
		// not successful didnt manage to store generated key in keystore
		KeyStore ks = CryptoUtil.getKeyStore("resource/test.jks", "JKS", "SUN", "changeit"); 
		CryptoUtil.loadKeyStore(ks, "resource/test.jks", "changeit");
		if (log.isInfoEnabled()) {
			log.info("ks" + ks);
		}
		try {
			Key key = ks.getKey("ctcrs", "changeit".toCharArray());
			if (log.isInfoEnabled()) {
				log.info("key: " + key);
			}
			
			KeyPair kp = CryptoUtil.generateKeyPair("RSA", 1024);
			if (log.isInfoEnabled()) {
				log.info("KeyPair: " + kp);
			}
			Certificate[] cert = CryptoUtil.generateCertChainFromKeyPair(kp, "RSA");
			if (log.isInfoEnabled()) {
				log.info("cert: " + cert + " " + ks.getCertificate(""));
			}
			//CryptoUtil.setKeyPairInKeyStore(kp, "RSA", "test", ks, "changeit");
			key = ks.getKey("test", "changeit".toCharArray());
			if (log.isInfoEnabled()) {
				log.info("key: " + key);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("", e);
			}
		}
	}
	
	public static void testSignature() {
		try {
			String testData = "Hello World";
			//KeyStore p11KeyStore = KeyStore.getInstance("PKCS11");
			//p11KeyStore.load(null, new char[] {'1', '2', '3', '4'});
			
			
			KeyStore ks = KeyStore.getInstance("JKS");
			CryptoUtil.loadKeyStore(ks, "resource/test.jks", "changeit");
			String myAlias = "test";
			
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initSign((PrivateKey) ks.getKey(myAlias, "changeit".toCharArray()));
			sig.update(testData.getBytes());
			
			byte[] signatureBytes = sig.sign();
			sig.initVerify(ks.getCertificate(myAlias));
			sig.update(testData.getBytes());
			if (sig.verify(signatureBytes))
				System.out.println("Signature verified");
			else
				System.out.println("Signature NOT verified");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void testConvertEncoded() {
		try {
			String algor = "RSA";
			KeyPair kp = CryptoUtil.generateKeyPair("RSA", 1024);
			Key pubKey = kp.getPublic();
			byte[] pubKeyEncoded = pubKey.getEncoded();
			
			String pubKeyEnc = ConvertUtil.convertByteArrayToString(pubKeyEncoded);
			System.out.println(pubKeyEnc);
			byte[] pubKeyEncoded2 = ConvertUtil.convertStringToByteArray(pubKeyEnc);
			
			System.out.println(pubKeyEnc.length());
			System.out.println(pubKeyEncoded.length + ":" + pubKeyEncoded2.length);
			
			X509EncodedKeySpec keySpec509 = new X509EncodedKeySpec(pubKeyEncoded2);
			PublicKey pubKey2 = KeyFactory.getInstance(algor).generatePublic(keySpec509);
			
			if (pubKey.equals(pubKey2)) {
				System.out.println("OK!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public static void testUsage() {
		String toBeEnDecrypted = "The quick brown fox jumps over the lazy dog.";
		try {
			KeyPair kp = CryptoUtil.generateKeyPair("RSA", 1024);
			Key pubKey = kp.getPublic();
			Key priKey = kp.getPrivate();
			
			String wrappedString = CryptoUtil.wrap(kp, "RSA", 1024, "ECB", "PKCS1Padding");
			Map map = CryptoUtil.unwrap(wrappedString);
			
			String s = CryptoUtil.encrypt(wrappedString, toBeEnDecrypted);
			String s1 = CryptoUtil.decrypt(wrappedString, s);
			System.out.println("==================================================");
			System.out.println(wrappedString);
			System.out.println(s);
			System.out.println(s1);
			System.out.println("==================================================");
			

			wrappedString = CryptoUtil.wrap(kp, "RSA", 1024);
			map = CryptoUtil.unwrap(wrappedString);
			
			s = CryptoUtil.encrypt(wrappedString, toBeEnDecrypted);
			s1 = CryptoUtil.decrypt(wrappedString, s);
			System.out.println("==================================================");
			System.out.println(wrappedString);
			System.out.println(s);
			System.out.println(s1);
			System.out.println("==================================================");

			
			Key k = CryptoUtil.generateKey("AES", 256);
			wrappedString = CryptoUtil.wrap(k, "AES", 256, "CBC", "PKCS5Padding");
			map = CryptoUtil.unwrap(wrappedString);
			
			s = CryptoUtil.encrypt(wrappedString, toBeEnDecrypted);
			s1 = CryptoUtil.decrypt(wrappedString, s);
			System.out.println("==================================================");
			System.out.println(wrappedString);
			System.out.println(s);
			System.out.println(s1);
			System.out.println("==================================================");
			
			wrappedString = CryptoUtil.wrap(k, "AES", 256);
			map = CryptoUtil.unwrap(wrappedString);
			
			s = CryptoUtil.encrypt(wrappedString, toBeEnDecrypted);
			s1 = CryptoUtil.decrypt(wrappedString, s);
			System.out.println("==================================================");
			System.out.println(wrappedString);
			System.out.println(s);
			System.out.println(s1);
			System.out.println("==================================================");

		
		} catch (Exception e) {
		}
	}
	
	
	public static void testHashingNoGood() {
		try {
			KeyPair keyPair = KeyPairGenerator.getInstance("RSA","BC").generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			PublicKey puKey = keyPair.getPublic();
			String plaintext = "This is the message being signed";
			Signature instance = Signature.getInstance("SHA1withRSA","BC");
			instance.initSign(privateKey);
			instance.update((plaintext).getBytes());
			byte[] signature = instance.sign();
			
			MessageDigest digest = MessageDigest.getInstance("SHA1", "BC");
			byte[] hash = digest.digest((plaintext).getBytes());
			
			//MessageDigest sha1 = MessageDigest.getInstance("SHA1","BC");
			//byte[] digest = sha1.digest((plaintext).getBytes());
			AlgorithmIdentifier digestAlgorithm = new AlgorithmIdentifier(new DERObjectIdentifier("1.3.14.3.2.26"), null);
			
			// create the digest info
			DigestInfo di = new DigestInfo(digestAlgorithm, hash);
			byte[] digestInfo = di.getDEREncoded();
			
			Cipher cipher = Cipher.getInstance("RSA","BC");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] cipherText = cipher.doFinal(digestInfo);
			
			//byte[] cipherText = cipher.doFinal(digest2);
			Cipher cipher2 = Cipher.getInstance("RSA","BC");
			cipher2.init(Cipher.DECRYPT_MODE, puKey);
			byte[] cipherText2 = cipher2.doFinal(signature);
			
			System.out.println("Input data: " + plaintext);
			System.out.println("Digest: " + new String(hash));
			System.out.println("Signature: " + ConvertUtil.convertByteArrayToString(signature));
			System.out.println("Signature2: " + ConvertUtil.convertByteArrayToString(cipherText));
			System.out.println("DigestInfo: " + ConvertUtil.convertByteArrayToString(digestInfo));
			System.out.println("Signature Decipher: " + ConvertUtil.convertByteArrayToString(cipherText2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void testHashing() {
		try {
			/* wrong version
			// get an instance of a cipher with RSA with ENCRYPT_MODE
			// Init the signature with the private key
			// Compute signature
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, thePrivateKey);
			
			Signature instance = Signature.getInstance("MD5withRSA");
			instance.initSign(thePrivateKey);
			
			// get an instance of the java.security.MessageDigest with MD5
			// process the digest
			MessageDigest md5_digest = MessageDigest.getInstance("MD5");
			md5_digest.update(aMessage);
			byte[] digest = md5_digest.digest();
			// return the encrypted digest
			byte[] cipherText = cipher.doFinal(digest);
			instance.update(cipherText);            
			byte[] signedMSG = instance.sign();
			//*/
			
			KeyPair kp = KeyPairGenerator.getInstance("RSA","BC").generateKeyPair();
			PrivateKey priKey = kp.getPrivate();
			PublicKey pubKey = kp.getPublic();

            String msg = "Message to be signed";
            System.out.println("Message: " + msg);
            byte[] msgBytes = msg.getBytes();
			
			// get an instance of a cipher with RSA with ENCRYPT_MODE
			// Init the signature with the private key
			// Compute signature
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, priKey);
			
			// get an instance of the java.security.MessageDigest with MD5
			// process the digest
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			byte[] digest = md5Digest.digest(msgBytes);
			System.out.println("Digest: " + ConvertUtil.convertByteArrayToString(digest));
			
			// return the encrypted digest
			byte[] cipherText = cipher.doFinal(digest);
			System.out.println("EncDigest: " + ConvertUtil.convertByteArrayToString(cipherText));


			//check sig
			// get an instance of a cipher with RSA with ENCRYPT_MODE
			// Init the signature with the private key
			// decrypt the signature
			
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			byte[] decDigest =  cipher.doFinal(cipherText);
			System.out.println("DecDigest: " + ConvertUtil.convertByteArrayToString(decDigest));
			
			// get an instance of the java.security.MessageDigest with MD5
			md5Digest = MessageDigest.getInstance("MD5");
			
			// process the digest
			md5Digest.update(msgBytes);
			digest = md5Digest.digest();
			System.out.println("ReDigest: " + ConvertUtil.convertByteArrayToString(digest));
			
			// check if digest1 == digest2
			if (decDigest == digest) {
				System.out.println("Same: OK");
			} else {
				System.out.println("Not Same: Not OK");
			}

            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		
		CryptoUtil.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		CryptoUtil.addProvider(new com.sun.crypto.provider.SunJCE());
		
		//name = FooAccelerator
		//library = /opt/foo/lib/libpkcs11.so
		/*
		String configName = "src/others/pkcs11.properties";
		Provider p = new sun.security.pkcs11.SunPKCS11(configName);
		Security.addProvider(p);
		//CryptoUtil.addProvider(new sun.security.pkcs11.SunPKCS11(configName));
		//*/
		
		
		
		//CryptoUtil.test();
		//CryptoUtil.testKeyStore();
		//CryptoUtil.testSignature();
		//XCryptoUtil.testConvertEncoded();
		//CryptoUtil.testUsage();
		
		CryptoUtil.testHashing();
	}
}
/**
Security.addProvider(new BouncyCastleProvider());
Hashtable subject = new Hashtable();
subject.put(X509Principal.CN, "Nombre del Profesor");
subject.put(X509Principal.OU, "Departamento");
subject.put(X509Principal.O, "School");
subject.put(X509Principal.L, "Ciudad");
subject.put(X509Principal.ST, "Estado");
subject.put(X509Principal.C, "MX");


Hashtable issuer = new Hashtable();
issuer.put(X509Principal.OU, "ou");
issuer.put(X509Principal.O, "org");
issuer.put(X509Principal.L, "Mexico");
issuer.put(X509Principal.ST, "DF");
issuer.put(X509Principal.C, "MX");

X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
certGen.setSerialNumber(BigInteger.valueOf(2));
certGen.setIssuerDN(new X509Principal(issuer));
certGen.setNotBefore(new Date(System.currentTimeMillis() - 10 * 60 * 1000));
certGen.setNotAfter(new Date(System.currentTimeMillis() + (60 * (24 * 60 * 60 * 1000))));
certGen.setSubjectDN(new X509Principal(subject));
certGen.setPublicKey(keyPair.getPublic());
certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
//certGen.setSignatureAlgorithm("RSA");

X509Certificate cert = certGen.generate(keyPair.getPrivate());
cert.checkValidity(new Date());
cert.verify(keyPair.getPublic());

FileOutputStream fos = new FileOutputStream(new File("C:\\certificate.cer"));
fos.write(cert.getEncoded());
fos.flush();
fos.close();

*/