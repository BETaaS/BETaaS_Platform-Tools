/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package eu.betaas.taas.securitymanager.authentication.service.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.osgi.framework.BundleContext;

import eu.betaas.taas.securitymanager.authentication.service.IEncryptDecryptService;
import eu.betaas.taas.securitymanager.authentication.utils.AuthBetaasBus;

/**
 * Implementation class of the IEncryptDecryptService interface
 * 
 * @author Bayu Anggorojati [ba@es.aau.dk]
 * Center for TeleInFrastruktur, Aalborg University, Denmark
 *
 */
public class EncryptDecryptService implements IEncryptDecryptService {
	/** Logger */
	private Logger log = Logger.getLogger("betaas.taas.securitymanager");
	
	/** Class that handles BETaaS BUS in authentication bundle */
	private AuthBetaasBus bus;
	
	/** Reference to Blueprint BundleContext */
	private BundleContext context;
	
	/**
	 * Initial setup method to initialize betaas bus service
	 */
	public void setup(){
		// set the GW ID
		bus = new AuthBetaasBus(context);
	}

	public String doEncryption(byte[] keyBytes, String inputString) {
		byte[] inputBytes = inputString.getBytes();
		return doEncryption(keyBytes, inputBytes);
	}

	public String doEncryption(byte[] keyBytes, byte[] inputBytes) {
		Key key;
    Cipher out;
    
    key = new SecretKeySpec(keyBytes, "AES");
    
    IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
    
    try {
    	log.debug("initialize encryption...");
			out = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			log.debug("still initialize encryption...");
			// initialize the encryption
			out.init(Cipher.ENCRYPT_MODE, key, ivSpec);
//			log.debug("before actual encryption...");
			byte[] result = out.doFinal(inputBytes);
			
			log.info("Encryption is done!");
			bus.sendData("Encryption is done", "info", "SecM");
			
			return new String(Base64.encode(result));
		} catch (NoSuchAlgorithmException e) {
			log.error("Encryption error: " + e.getMessage());
			bus.sendData("Encryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (NoSuchPaddingException e) {
			log.error("Encryption error: " + e.getMessage());
			bus.sendData("Encryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (InvalidKeyException e) {
			log.error("Encryption error: " + e.getMessage());
			bus.sendData("Encryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			log.error("Encryption error: " + e.getMessage());
			bus.sendData("Encryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (IllegalBlockSizeException e) {
			log.error("Encryption error: " + e.getMessage());
			bus.sendData("Encryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (BadPaddingException e) {
			log.error("Encryption error: " + e.getMessage());
			bus.sendData("Encryption error" + e.getMessage(), "error", "SecM");
			return null;
		}
	}

	public String doDecryption(byte[] keyBytes, String encrypted) {
		Key key;
    Cipher in;
    
    key = new SecretKeySpec(keyBytes, "AES");
    IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
    
    try {
    	log.debug("initialize decryption...");
			in = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// initialize the decryption
			in.init(Cipher.DECRYPT_MODE, key,ivSpec);
			
			byte[] result = in.doFinal(Base64.decode(encrypted));
			
			log.info("Decryption is done!");
			bus.sendData("Decryption is done", "info", "SecM");
			
			return new String(result);
		} catch (NoSuchAlgorithmException e) {
			log.error("Decryption error: " + e.getMessage());
			bus.sendData("Decryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (NoSuchPaddingException e) {
			log.error("Decryption error: " + e.getMessage());
			bus.sendData("Decryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (InvalidKeyException e) {
			log.error("Decryption error: " + e.getMessage());
			bus.sendData("Decryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			log.error("Decryption error: " + e.getMessage());
			bus.sendData("Decryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (IllegalBlockSizeException e) {
			log.error("Decryption error: " + e.getMessage());
			bus.sendData("Decryption error" + e.getMessage(), "error", "SecM");
			return null;
		} catch (BadPaddingException e) {
			log.error("Decryption error: " + e.getMessage());
			bus.sendData("Decryption error" + e.getMessage(), "error", "SecM");
			return null;
		}
	}
	
	/**
	 * Blueprint set reference to BundleContext
	 * @param context BundleContext
	 */
	public void setContext(BundleContext context) {
		this.context = context;
		log.debug("Got BundleContext from the blueprint...");
	}

}
