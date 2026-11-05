plugins { id("org.jetbrains.kotlin.jvm") }
kotlin { jvmToolchain(17) }
dependencies { testImplementation(kotlin("test")) }
tasks.test { useJUnitPlatform() }
dependencies { testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3") }
