plugins {
    id("java")
}

group = "org.faya.sensei"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")

    implementation(platform("org.glassfish.jersey:jersey-bom:3.1.7"))
    implementation("org.glassfish.jersey.inject:jersey-hk2")
    implementation("org.glassfish.jersey.media:jersey-media-json-processing")
    implementation("org.glassfish.jersey.containers:jersey-container-jdk-http")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.reflections:reflections:0.9.12")

    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
}

tasks.test {
    useJUnitPlatform()
}