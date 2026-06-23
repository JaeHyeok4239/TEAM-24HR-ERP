package com.hr24.global.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

@Service
public class CryptoService {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int GCM_TAG_LENGTH = 128;
	private static final int IV_LENGTH = 12;

	private final SecureRandom secureRandom = new SecureRandom();
	private final SecretKeySpec secretKeySpec;

	public CryptoService(CryptoProperties cryptoProperties) {
		byte[] keyBytes = Base64.getDecoder().decode(cryptoProperties.getSecretKey());

		if (!isValidAesKeyLength(keyBytes.length)) {
			throw new IllegalArgumentException("AES 키는 16, 24, 32바이트 중 하나여야 합니다.");
		}

		this.secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public String encrypt(String plainText) {
		if (plainText == null || plainText.isBlank()) {
			return null;
		}

		try {
			byte[] iv = generateIv();

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

			byte[] encryptedBytes = cipher.doFinal(
					plainText.getBytes(StandardCharsets.UTF_8)
			);

			ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
			byteBuffer.put(iv);
			byteBuffer.put(encryptedBytes);

			return Base64.getEncoder().encodeToString(byteBuffer.array());
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.CRYPTO_PROCESSING_FAILED);
		}
	}

	public String decrypt(String encryptedText) {
		if (encryptedText == null || encryptedText.isBlank()) {
			return null;
		}

		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

			ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);

			byte[] iv = new byte[IV_LENGTH];
			byteBuffer.get(iv);

			byte[] cipherText = new byte[byteBuffer.remaining()];
			byteBuffer.get(cipherText);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);

			byte[] plainBytes = cipher.doFinal(cipherText);

			return new String(plainBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.CRYPTO_PROCESSING_FAILED);
		}
	}

	private byte[] generateIv() {
		byte[] iv = new byte[IV_LENGTH];
		secureRandom.nextBytes(iv);
		return iv;
	}

	private boolean isValidAesKeyLength(int keyLength) {
		return keyLength == 16 || keyLength == 24 || keyLength == 32;
	}
}