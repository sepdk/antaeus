plugins {
    kotlin("jvm")
}

kotlinProject()

dataLibs()

dependencies {
    implementation(project(":pleo-antaeus-models"))
    implementation(project(":pleo-antaeus-data"))
}
