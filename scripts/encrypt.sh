#!/bin/bash

# Jasypt 암호화 유틸리티 스크립트
# 사용법: ./scripts/encrypt.sh "암호화할_문자열"
# 
# 환경변수 JASYPT_ENCRYPTOR_PASSWORD가 설정되어 있어야 합니다.
# 예: export JASYPT_ENCRYPTOR_PASSWORD=your-secret-key

if [ -z "$1" ]; then
    echo "사용법: $0 \"암호화할_문자열\""
    echo ""
    echo "예시:"
    echo "  export JASYPT_ENCRYPTOR_PASSWORD=my-secret-key"
    echo "  $0 \"my-password\""
    exit 1
fi

if [ -z "$JASYPT_ENCRYPTOR_PASSWORD" ]; then
    echo "오류: 환경변수 JASYPT_ENCRYPTOR_PASSWORD가 설정되지 않았습니다."
    echo ""
    echo "사용법:"
    echo "  export JASYPT_ENCRYPTOR_PASSWORD=your-secret-key"
    echo "  $0 \"암호화할_문자열\""
    exit 1
fi

# Java 클래스패스에 Jasypt 라이브러리가 필요합니다
# Gradle을 통해 빌드된 경우 사용 가능
if [ -f "build/libs"/*.jar ]; then
    JAR_FILE=$(ls build/libs/*.jar | head -n 1)
    java -cp "$JAR_FILE:$(./gradlew -q printClasspath 2>/dev/null || echo '')" \
         org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
         input="$1" \
         password="$JASYPT_ENCRYPTOR_PASSWORD" \
         algorithm=PBEWITHHMACSHA512ANDAES_256
else
    # 간단한 방법: Gradle task 사용
    echo "Gradle을 통해 암호화를 수행합니다..."
    ./gradlew -q --console=plain encryptValue -Pvalue="$1" -Ppassword="$JASYPT_ENCRYPTOR_PASSWORD" 2>/dev/null || {
        echo ""
        echo "대안: Java 코드로 직접 암호화하거나, 온라인 도구를 사용하세요."
        echo ""
        echo "또는 다음 Java 코드를 사용하세요:"
        echo "  import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;"
        echo "  StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();"
        echo "  encryptor.setPassword(\"your-secret-key\");"
        echo "  encryptor.setAlgorithm(\"PBEWITHHMACSHA512ANDAES_256\");"
        echo "  String encrypted = encryptor.encrypt(\"$1\");"
        echo "  System.out.println(\"ENC(\" + encrypted + \")\");"
    }
fi
