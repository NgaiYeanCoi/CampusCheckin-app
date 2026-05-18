plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "cn.nyc1"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.mybatis.spring.boot.starter)
    implementation(libs.sa.token.spring.boot3.starter)
    implementation(libs.spring.security.crypto)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    runtimeOnly(libs.mysql.connector.j)
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
