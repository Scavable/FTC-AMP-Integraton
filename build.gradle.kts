plugins {
    id("java")

    //Gradle shadow plugin to make fatjar
    id("com.github.johnrengelman.shadow") version ("7.0.0")
}

group = "com.scavable"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.taimos:totp:1.0")
    implementation("commons-codec:commons-codec:1.10")
    implementation("com.google.zxing:javase:3.2.1")
    implementation("org.json:json:20230227")
    implementation("com.discord4j:discord4j-core:3.2.5")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.scavable.Main")
    }

    finalizedBy("shadowJar")
}