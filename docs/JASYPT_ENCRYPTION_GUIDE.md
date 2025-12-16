# Jasypt 설정 파일 암호화 가이드

## 개요

Jasypt를 사용하여 `application.properties` 파일의 민감한 정보를 암호화하고 Git에 안전하게 저장할 수 있습니다.

## 장점과 단점

### ✅ 장점
- 설정 파일을 Git에 올릴 수 있어 배포가 간편함
- 민감한 정보가 암호화된 상태로 저장됨
- 팀원들과 설정 파일을 공유할 수 있음

### ⚠️ 단점
- **암호화 키(마스터 키) 관리가 매우 중요함**
  - 키가 유출되면 모든 암호화된 값이 노출됨
  - 키는 반드시 환경변수나 외부 시크릿 관리 시스템에서 주입해야 함
- 키를 안전하게 관리하는 추가 인프라가 필요할 수 있음

## 사용 방법

### 1. 암호화 키 설정

**절대 Git에 올리지 마세요!** 환경변수로만 관리합니다.

```bash
# 개발 환경
export JASYPT_ENCRYPTOR_PASSWORD=your-development-secret-key

# 운영 환경 (더 강력한 키 사용)
export JASYPT_ENCRYPTOR_PASSWORD=your-production-secret-key-very-long-and-secure
```

### 2. 값 암호화하기

#### 방법 1: Gradle Task 사용 (권장)

```bash
# 암호화
./gradlew encryptValue -Pvalue="my-password" -Ppassword="your-secret-key"

# 출력 예시:
# ENC(xyz123abc456...)
# 
# application.properties에 다음과 같이 사용하세요:
#   your.property=ENC(xyz123abc456...)
```

#### 방법 2: Java 코드 직접 사용

```java
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
encryptor.setPassword("your-secret-key");
encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
String encrypted = encryptor.encrypt("my-password");
System.out.println("ENC(" + encrypted + ")");
```

### 3. application.properties에 사용하기

```properties
# 암호화된 값 사용
spring.datasource.password=ENC(xyz123abc456...)
jwt.secret=ENC(abc789def012...)
```

### 4. 애플리케이션 실행

암호화 키를 환경변수로 주입하여 실행:

```bash
# 환경변수로 주입
export JASYPT_ENCRYPTOR_PASSWORD=your-secret-key
java -jar app.jar

# 또는 실행 시 직접 주입
java -jar app.jar --jasypt.encryptor.password=your-secret-key
```

### 5. Docker 환경에서 사용

```dockerfile
# Dockerfile에서 환경변수로 주입 (권장하지 않음 - 보안상 위험)
# ENV JASYPT_ENCRYPTOR_PASSWORD=your-secret-key

# 대신 docker-compose나 Kubernetes Secret 사용
```

```yaml
# docker-compose.yml 예시
services:
  app:
    image: your-app:latest
    environment:
      - JASYPT_ENCRYPTOR_PASSWORD=${JASYPT_ENCRYPTOR_PASSWORD}
    # 또는 secrets 사용
    secrets:
      - jasypt_password
```

## 보안 권장사항

1. **암호화 키 관리**
   - 개발/테스트/운영 환경마다 다른 키 사용
   - 키는 최소 32자 이상의 랜덤 문자열 권장
   - 키는 환경변수, Kubernetes Secrets, AWS Secrets Manager 등으로 관리

2. **키 생성 방법**
   ```bash
   # Linux/Mac
   openssl rand -base64 32
   
   # 또는
   cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1
   ```

3. **Git 관리**
   - `application.properties`는 Git에 올려도 됨 (암호화되어 있으므로)
   - **하지만 암호화 키는 절대 Git에 올리지 마세요!**
   - `.gitignore`에 키 관련 파일이 있는지 확인

4. **환경별 키 관리**
   - 개발: 개발자 로컬 환경변수
   - 테스트: CI/CD 파이프라인의 Secret 변수
   - 운영: Kubernetes Secrets, AWS Secrets Manager 등

## 문제 해결

### "Jasypt 암호화 키가 설정되지 않았습니다" 오류

환경변수 `JASYPT_ENCRYPTOR_PASSWORD`가 설정되지 않았습니다.

```bash
export JASYPT_ENCRYPTOR_PASSWORD=your-secret-key
```

### 복호화 실패

암호화 키가 잘못되었거나, 암호화된 값이 손상되었을 수 있습니다.

```bash
# 복호화 테스트
./gradlew decryptValue -Pvalue="ENC(암호화된값)" -Ppassword="암호화키"
```

## 참고

- [Jasypt 공식 문서](https://github.com/ulisesbocchio/jasypt-spring-boot)
- [Spring Boot 3.x 호환 버전](https://github.com/ulisesbocchio/jasypt-spring-boot/releases)
