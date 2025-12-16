package site.jejinni.server.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jasypt 암호화 설정
 * 
 * 암호화 키는 환경변수 JASYPT_ENCRYPTOR_PASSWORD로 주입받습니다.
 * 예: export JASYPT_ENCRYPTOR_PASSWORD=your-secret-key
 * 
 * 또는 애플리케이션 실행 시:
 * java -jar app.jar --jasypt.encryptor.password=your-secret-key
 */
@Configuration
public class JasyptConfig {

	@Value("${jasypt.encryptor.password:}")
	private String encryptorPassword;

	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		
		// 환경변수에서 암호화 키 가져오기
		String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
		if (password == null || password.isEmpty()) {
			password = encryptorPassword;
		}
		
		if (password == null || password.isEmpty()) {
			throw new IllegalStateException(
				"Jasypt 암호화 키가 설정되지 않았습니다. " +
				"환경변수 JASYPT_ENCRYPTOR_PASSWORD를 설정하거나 " +
				"jasypt.encryptor.password 프로퍼티를 설정해주세요."
			);
		}
		
		config.setPassword(password);
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
		config.setStringOutputType("base64");
		
		encryptor.setConfig(config);
		return encryptor;
	}
}
