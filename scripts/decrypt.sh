#!/bin/bash

# Jasypt 복호화 유틸리티 스크립트
# 사용법: ./scripts/decrypt.sh "ENC(암호화된_문자열)"
# 
# 환경변수 JASYPT_ENCRYPTOR_PASSWORD가 설정되어 있어야 합니다.

if [ -z "$1" ]; then
    echo "사용법: $0 \"ENC(암호화된_문자열)\""
    exit 1
fi

if [ -z "$JASYPT_ENCRYPTOR_PASSWORD" ]; then
    echo "오류: 환경변수 JASYPT_ENCRYPTOR_PASSWORD가 설정되지 않았습니다."
    exit 1
fi

# ENC() 제거
ENCRYPTED_VALUE=$(echo "$1" | sed 's/ENC(//' | sed 's/)//')

echo "복호화 중..."
echo "원본 값: $ENCRYPTED_VALUE"

# 실제 복호화는 Java 코드로 수행해야 합니다
echo ""
echo "복호화는 Java 코드로 수행해야 합니다:"
echo "  import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;"
echo "  StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();"
echo "  encryptor.setPassword(\"your-secret-key\");"
echo "  encryptor.setAlgorithm(\"PBEWITHHMACSHA512ANDAES_256\");"
echo "  String decrypted = encryptor.decrypt(\"$ENCRYPTED_VALUE\");"
