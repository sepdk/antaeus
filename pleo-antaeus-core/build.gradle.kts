plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    api(project(":pleo-antaeus-data"))
    api(project(":pleo-antaeus-logger"))
    api(project(":pleo-antaeus-service"))
    api(project(":pleo-antaeus-models"))
}