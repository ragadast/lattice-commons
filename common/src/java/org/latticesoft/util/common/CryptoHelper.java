/*
 * Copyright 2004 Senunkan Shinryuu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Apr 19, 2007
 *
 */
package org.latticesoft.util.common;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import javax.crypto.spec.*;
import javax.crypto.*;
//import org.bouncycastle.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CryptoHelper implements Serializable {
	private static final Log log = LogFactory.getLog(CryptoHelper.class);
	public static final long serialVersionUID = 20080401150954L;
	private KeyPair keyPair = null;
	private int strength = 1024;
	private String algorithm = "RSA";
	private String padding = "PKCS1Padding";
	private String mode = "ECB";
	private Provider provider = null;
	private String providerName = "BC";
	private String secureRandomProviderName = "SUN";
	private String secureRandomAlgorithm = "SHA1PRNG";
	
	public void init() {
		if (this.provider != null) {
			Security.addProvider(provider);			
		}
		this.generateKeyPair();
	}
	
	public void generateKeyPair(int strength) {
		this.strength = strength;
		this.generateKeyPair();
	}
	
	public void generateKeyPair() {
		this.keyPair = CryptoUtil.generateKeyPair(this.algorithm, this.strength);
	}
	
	public String getInstanceAlgorithm() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.algorithm).append("/");
		sb.append(this.mode).append("/");
		sb.append(this.padding);
		return sb.toString();
	}
	
	public String convertKeyToString() {
		StringBuffer sb = new StringBuffer();
		Cipher cipher = null;
		byte[] priKeyByte = null;
		byte[] pubKeyByte = null;
		String priKeyString = null;
		String pubKeyString = null;
		try {
			cipher = Cipher.getInstance(this.getInstanceAlgorithm(), this.providerName);
			priKeyByte = cipher.wrap(this.keyPair.getPrivate());
			pubKeyByte = cipher.wrap(this.keyPair.getPublic());
			priKeyString = NumeralUtil.toHexString(priKeyByte);
			pubKeyString = NumeralUtil.toHexString(pubKeyByte);
			sb.append("algor=").append(this.getInstanceAlgorithm()).append("|");
			sb.append("strength=").append(this.strength).append("|");
			sb.append("priKey=").append(priKeyString).append("|");
			sb.append("pubKey=").append(pubKeyString).append("|");
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error storing key");
			}
		}
		return sb.toString();
	}
	
	public void convertKeyFromString(String keyString) {
		
	}
	
	public String encode(String input) {
		if (this.keyPair == null || input == null || input.length() == 0) {
			return "";
		}
		int index = 0;
		int diff = 0;
		StringBuffer sb = new StringBuffer();
		String algor = this.getInstanceAlgorithm();
		PublicKey pubKey = null;
		SecureRandom srand = null;
		Cipher rsaEnc = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		byte[] data = input.getBytes();
		int bufferSize = strength/32;
		byte[] buffer = new byte[bufferSize];
		byte[] tmp = null;
		String output = null;

		try {
			pubKey = this.keyPair.getPublic();
			srand = SecureRandom.getInstance(this.secureRandomAlgorithm, this.secureRandomProviderName);
			rsaEnc = Cipher.getInstance(algor, this.providerName);
			rsaEnc.init(Cipher.ENCRYPT_MODE, pubKey, srand);
		
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			while (index < data.length) {
				diff = data.length - index;
				if (diff > buffer.length) {
					diff = buffer.length;
				}
				NumeralUtil.resetByteArray(buffer);
				System.arraycopy(data, index, buffer, 0, diff);
				tmp = rsaEnc.doFinal(buffer, 0, buffer.length);
				if (tmp != null) {
					dos.writeInt(tmp.length);
					dos.write(tmp);
					NumeralUtil.resetByteArray(tmp);
				}
				tmp = null;//*/
				index += diff;
			}
			output = NumeralUtil.toHexString(baos.toByteArray());
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in encryption", e);
			}
		}
		return output;
	}
}
