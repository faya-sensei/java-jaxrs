plugins {
    application
}

group = "org.faya.sensei"
version = "1.0-SNAPSHOT"

application {
    mainClass = "org.faya.sensei.App"
}

repositories {
    mavenCentral()
}

dependencies {
    // Database & ORM
    implementation("com.h2database:h2:2.2+")
    implementation("org.hibernate.orm:hibernate-core:6.5.+")

    // Jakarta EE API
    implementation("jakarta.platform:jakarta.jakartaee-api:10.0.+")

    // Jersey dependencies
    implementation(platform("org.glassfish.jersey:jersey-bom:3.1+"))
    implementation("org.glassfish.jersey.inject:jersey-hk2")
    implementation("org.glassfish.jersey.media:jersey-media-json-binding")
    implementation("org.glassfish.jersey.media:jersey-media-json-processing")
    implementation("org.glassfish.jersey.media:jersey-media-sse")
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http")

    // Reflection
    implementation("org.reflections:reflections:0.10.+")

    // Json web token
    implementation("com.auth0:java-jwt:4.4.+")

    // Testing dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.+"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.12.+")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.+")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
