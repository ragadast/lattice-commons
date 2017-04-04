/*
 * Copyright Jurong Port Pte Ltd
 * Created on Jul 29, 2009
 */
package org.latticesoft.util.common;

import java.util.*;
import java.io.Serializable;
import java.security.*;
import java.security.cert.Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Crypto implements Serializable {
	public static final long serialVersionUID = 20090729165158L;
	private static final Log log = LogFactory.getLog(Crypto.class);
	
	public static final String RSA = "RSA";
	
	private KeyPair keyPair;
	private int strength = 1024;
	private String algorithm = "RSA";
	private String padding;
	private String mode;
	private String keyWrapped;
	private Key key;
	private Map keyMap = null;
	
	private String keyStoreType;
	private String keyStoreProvider;
	private String keyStoreLocation;
	private String keyStoreAlias;
	private String keyStorePassword;
	private boolean useKeyStore = false;
	
	private boolean ready;
	
	public Crypto() {}
	public Crypto(Crypto that) {
		this();
		if (that != null) {
			this.setAlgorithm(that.getAlgorithm());
			this.setKeyWrapped(that.getKeyWrapped());
			this.setKey(that.getKey());
			this.setKeyPair(that.getKeyPair());
			Map map = new HashMap();
			map.putAll(that.getKeyMap());
			this.setKeyMap(map);
			
			this.setKeyStoreAlias(that.getKeyStoreAlias());
			this.setKeyStoreLocation(that.getKeyStoreLocation());
			this.setKeyStorePassword(that.getKeyStorePassword());
			this.setKeyStoreProvider(that.getKeyStoreProvider());
			this.setKeyStoreType(that.getKeyStoreType());
		}
	}
	
	/** @return Returns the keyPair. */
	public KeyPair getKeyPair() {
		return (this.keyPair);
	}
	/** @param keyPair The keyPair to set. */
	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	/** @return Returns the strength. */
	public int getStrength() {
		return (this.strength);
	}
	/** @param strength The strength to set. */
	public void setStrength(int strength) {
		this.strength = strength;
	}
	/** @return Returns the algorithm. */
	public String getAlgorithm() {
		return (this.algorithm);
	}
	/** @param algorithm The algorithm to set. */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	/** @return Returns the padding. */
	public String getPadding() {
		return (this.padding);
	}
	/** @param padding The padding to set. */
	public void setPadding(String padding) {
		this.padding = padding;
	}
	/** @return Returns the mode. */
	public String getMode() {
		return (this.mode);
	}
	/** @param mode The mode to set. */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/** @return Returns the keyString. */
	public String getKeyWrapped() {
		return (this.keyWrapped);
	}
	/** @param keyString The keyString to set. */
	public void setKeyWrapped(String keyString) {
		this.keyWrapped = keyString;
	}
	/** @return Returns the key. */
	public Key getKey() {
		return (this.key);
	}
	/** @param key The key to set. */
	public void setKey(Key key) {
		this.key = key;
	}
	/** @return Returns the keystore. */
	public String getKeyStoreLocation() {
		return (this.keyStoreLocation);
	}
	/** @param keystore The keystore to set. */
	public void setKeyStoreLocation(String keystore) {
		this.keyStoreLocation = keystore;
	}
	/** @return Returns the initialised. */
	public boolean isReady() {
		return (this.ready);
	}
	/** @return Returns the initialised. */
	public boolean getReady() {
		return (this.ready);
	}
	
	/** @return Returns the keyStoreAlias. */
	public String getKeyStoreAlias() {
		return (this.keyStoreAlias);
	}
	/** @param keyStoreAlias The keyStoreAlias to set. */
	public void setKeyStoreAlias(String keyStoreAlias) {
		this.keyStoreAlias = keyStoreAlias;
	}
	/** @return Returns the keyStorePassword. */
	public String getKeyStorePassword() {
		return (this.keyStorePassword);
	}
	/** @param keyStorePassword The keyStorePassword to set. */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	/** @return Returns the useKeyStore. */
	public boolean isUseKeyStore() {
		return (this.useKeyStore);
	}
	/** @return Returns the useKeyStore. */
	public boolean getUseKeyStore() {
		return (this.useKeyStore);
	}
	/** @param useKeyStore The useKeyStore to set. */
	public void setUseKeyStore(boolean useKeyStore) {
		this.useKeyStore = useKeyStore;
	}
	/** @return Returns the keyStoreType. */
	public String getKeyStoreType() {
		return (this.keyStoreType);
	}
	/** @param keyStoreType The keyStoreType to set. */
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}
	/** @return Returns the keyStoreProvider. */
	public String getKeyStoreProvider() {
		return (this.keyStoreProvider);
	}
	/** @param keyStoreProvider The keyStoreProvider to set. */
	public void setKeyStoreProvider(String keyStoreProvider) {
		this.keyStoreProvider = keyStoreProvider;
	}
	/** @return Returns the keyMap. */
	public Map getKeyMap() {
		return (this.keyMap);
	}
	/** @param keyMap The keyMap to set. */
	public void setKeyMap(Map keyMap) {
		this.keyMap = keyMap;
	}
	
	public void reset() {
		this.key = null;
		this.keyWrapped = null;
		this.keyPair = null;
		this.ready = false;
		this.strength = 1024;
		this.algorithm = "RSA";
	}
	
	
	public void generateKeyPair() {
		if (this.algorithm == null) {
			return;
		}
		this.keyPair = CryptoUtil.generateKeyPair(this.algorithm, this.strength);
		this.keyWrapped = CryptoUtil.wrap(this.keyPair, this.algorithm, this.strength);
		if (log.isDebugEnabled()) {
			log.debug("keyEncoded: " + this.keyWrapped);
		}
		this.keyMap = new HashMap();
		this.keyMap.put(CryptoUtil.WRAP_ALGORITHM, this.algorithm);
		this.keyMap.put(CryptoUtil.WRAP_KEYPAIR, this.keyPair);
		this.keyMap.put(CryptoUtil.WRAP_STRENGTH, "" + this.strength);
		this.ready =  true;
	}
	public void generateKey() {
		if (this.algorithm == null) {
			return;
		}
		this.key = CryptoUtil.generateKey(this.algorithm, this.strength);
		this.keyWrapped = CryptoUtil.wrap(this.key, this.algorithm, this.strength);
		this.keyMap = new HashMap();
		this.keyMap.put(CryptoUtil.WRAP_ALGORITHM, this.algorithm);
		this.keyMap.put(CryptoUtil.WRAP_KEY, this.key);
		this.keyMap.put(CryptoUtil.WRAP_STRENGTH, "" + this.strength);
		this.ready =  true;
	}

	public void init() {
		if (this.keyPair != null || this.key != null) {
			if (log.isInfoEnabled()) {
				log.info("Already initialised");
			}
			return;
		}
			
		this.ready = false;
		if (this.useKeyStore) {
			try {
				KeyStore ks = CryptoUtil.getKeyStore(this.keyStoreLocation, this.keyStoreType, this.keyStoreProvider, this.keyStorePassword);
				if (!ks.containsAlias(this.keyStoreAlias)) {
					return;
				}
				if (ks.isKeyEntry(this.keyStoreAlias)) {
					this.key = ks.getKey(this.keyStoreAlias, this.keyStorePassword.toCharArray());
				} else if (ks.isCertificateEntry(this.keyStoreAlias)) {
					Certificate cert = ks.getCertificate(this.keyStoreAlias);
					PublicKey pubKey = cert.getPublicKey();
					KeyStore.PasswordProtection kspwdprot = new KeyStore.PasswordProtection(this.keyStorePassword.toCharArray());
					KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(this.keyStoreAlias + "pk", kspwdprot);
					PrivateKey priKey = pkEntry.getPrivateKey();
					this.keyPair = new KeyPair(pubKey, priKey);
				}
				this.keyMap = new HashMap();
				
				this.keyMap.put(CryptoUtil.WRAP_ALGORITHM, this.algorithm);
				if (this.key != null) {
					this.keyMap.put(CryptoUtil.WRAP_KEY, this.key);
				}
				if (this.keyPair != null) {
					this.keyMap.put(CryptoUtil.WRAP_KEYPAIR, this.keyPair);
				}
				if (this.keyWrapped != null) {
					this.keyMap.put(CryptoUtil.WRAP_KEY_ENCODED, this.keyWrapped);
				}
				
				
				
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error("Error when initialising keystore", e);
				}
			}
		} else {
			if (this.keyWrapped == null) {
				if (log.isInfoEnabled()) {
					log.info("Missing key info cannot initialise Crypto");
				}
				return;
			}
			this.keyMap = CryptoUtil.unwrap(this.keyWrapped);
			this.algorithm = CryptoUtil.unwrapAlgorithm(this.keyMap);
			if (CryptoUtil.isKeyPair(this.keyMap)) {
				this.keyPair = CryptoUtil.unwrapKeyPair(this.keyMap);
				this.key = null;
			} else {
				this.key = CryptoUtil.unwrapKey(this.keyMap);
				this.keyPair = null;
			}
		}
		this.ready = true;
	}
	
	public String encrypt(String s) {
		String retVal = null;
		if (!this.isReady() || s == null) {
			return retVal;
		}
		return CryptoUtil.encrypt(this.keyMap, s, true);
	}
	
	public String decrypt(String s) {
		String retVal = null;
		if (!this.isReady() || s == null) {
			return retVal;
		}
		return CryptoUtil.decrypt(this.keyMap, s, true);
	}

	public static void main(String[] args) {
		Crypto c = new Crypto();
		c.setAlgorithm("RSA");
		c.setStrength(1024);
		c.generateKeyPair();
		//c.init();
		String s = c.encrypt("The quick brown fox jumps over the lazy dog");
		System.out.println(s);
		String s1 = c.decrypt(s);
		System.out.println(s1);
		System.out.println(c.getKeyWrapped());
		
		c.reset();
		c.setAlgorithm("DESede");
		c.setStrength(168);
		c.generateKey();
		s = c.encrypt("The quick brown fox jumps over the lazy dog");
		System.out.println(s);
		s1 = c.decrypt(s);
		System.out.println(s1);
		System.out.println("==================================================");
		System.out.println(c.getKeyWrapped());
		
		//c.reset();
		System.out.println("==================================================");
		Crypto c2 = new Crypto();
		c2.setAlgorithm(c.getAlgorithm());
		c2.setKeyWrapped(c.getKeyWrapped());
		c2.init();
		
		s1 = c.decrypt(s);
		System.out.println(s1);
		System.out.println(c2.getKeyWrapped());
		System.out.println(c2.getKeyWrapped().equals(c.getKeyWrapped()));
		
		System.out.println("==================================================" + c2.isReady());
		s = c2.encrypt("pass1234");
		System.out.println(s);
		s1 = c2.decrypt(s);
		System.out.println(s1);
		
		
	}
}

