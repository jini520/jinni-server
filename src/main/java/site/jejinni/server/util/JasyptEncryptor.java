package site.jejinni.server.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * Jasypt 암호화/복호화 유틸리티
 * 
 * Gradle task로 사용:
 *   암호화: ./gradlew encryptValue -Pvalue="암호화할값" -Ppassword="암호화키"
 *   복호화: ./gradlew decryptValue -Pvalue="ENC(암호화된값)" -Ppassword="암호화키"
 */
public class JasyptEncryptor {
	
	private static final String ALGORITHM = "PBEWITHHMACSHA512ANDAES_256";
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("사용법:");
			System.err.println("  암호화: java JasyptEncryptor \"암호화할값\" \"암호화키\"");
			System.err.println("  복호화: java JasyptEncryptor \"ENC(암호화된값)\" \"암호화키\" --decrypt");
			System.exit(1);
		}
		
		String value = args[0];
		String password = args[1];
		boolean decrypt = args.length > 2 && "--decrypt".equals(args[2]);
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(password);
		encryptor.setAlgorithm(ALGORITHM);
		
		try {
			if (decrypt) {
				// ENC() 제거
				String encryptedValue = value.replace("ENC(", "").replace(")", "");
				String decrypted = encryptor.decrypt(encryptedValue);
				System.out.println("복호화된 값: " + decrypted);
			} else {
				String encrypted = encryptor.encrypt(value);
				System.out.println("ENC(" + encrypted + ")");
				System.out.println("");
				System.out.println("application.properties에 다음과 같이 사용하세요:");
				System.out.println("  your.property=ENC(" + encrypted + ")");
			}
		} catch (Exception e) {
			System.err.println("오류 발생: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
