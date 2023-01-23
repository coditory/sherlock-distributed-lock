description = "Sherlock Distributed Lock in-memory reactive implementation"

dependencies {
    api(project(":api:api-rxjava"))
    api(project(":inmem:inmem-sync"))
    integrationImplementation(project(":tests"))
}
