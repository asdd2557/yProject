import org.gradle.internal.impldep.org.bouncycastle.asn1.iana.IANAObjectIdentifiers.mail
import org.gradle.internal.impldep.org.bouncycastle.asn1.iana.IANAObjectIdentifiers.security

plugins {
	java
	id("org.springframework.boot") version "2.7.6"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies { //라이브러리 넣는곳
	//lombok
	compileOnly ("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	//thymeleaf
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	//DynamoDB
	implementation(platform("software.amazon.awssdk:bom:2.20.85"))
	implementation("software.amazon.awssdk:dynamodb-enhanced")
	//H2
	runtimeOnly("com.h2database:h2")
	//MySQL
	implementation("mysql:mysql-connector-java")
	//Maper
	implementation ("org.modelmapper:modelmapper:2.4.4")
	//security
	implementation ("org.springframework.boot:spring-boot-starter-security") //스프링 시큐리티를 사용하기 위한 스타터 추가
	implementation ("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:latest.release")//타임리프에서 스프링 시큐리티를 사용하기 위한 의존성 추가
	implementation ("org.springframework.security:spring-security-test")//스프링 시큐리티를 테스트하기 위한 의존성 추가
	//JWT
	implementation ("io.jsonwebtoken:jjwt:0.9.1") // 자바 JWT 라이브러리
	implementation ("javax.xml.bind:jaxb-api:2.3.1") // XML 문서와 Java 객체 간 매핑 자동화
	//OAuth2
	implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
	//jpa
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//mustache
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	//web
	implementation("org.springframework.boot:spring-boot-starter-web")
	//start test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks.withType<Test> {
	useJUnitPlatform()
}