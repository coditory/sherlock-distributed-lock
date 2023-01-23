description = "Sherlock Distributed Lock in-memory reactive implementation"

dependencies {
    api(project(":inmem:inmem-sync"))
    api(project(":api:api-reactor"))
    integrationImplementation(project(":tests"))
}
