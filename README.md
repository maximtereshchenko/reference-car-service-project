# What is it?

This is a reference project for Java intensive course.

## Required capabilities

* Add a repairer
* Remove a repairer
* List repairers sorted by name, status
* Add a garage slot
* Remove a garage slot
* List garage slots sorted by availability
* Create an order
* Assign a garage slot to the order
* Assign one or more repairers to the order
* Complete an order
* Cancel an order
* List orders sorted by creation date, completion date, price, status
* Get full information about an order: repairers, garage slot, creation date, completion date

## How to run?

NOTE: requires Docker and docker-compose plugin installed

1. Run docker-compose
    ```bash
    cd docker-compose
    docker-compose up
    ```
2. Add `127.0.0.1 keycloak` to `/etc/hosts`
3. Open https://oidcdebugger.com/ in the browser and fill in the form:
    * Authorize URI - http://keycloak:8080/realms/car-service/protocol/openid-connect/auth
    * Client ID - `public`
    * Response type - leave only `token` checked
    * Response mode - `form_post`
4. Login using credentials for any predefined users in [realm.json](./docker-compose/realm.json)
   (e.g. login `anna`, password `anna`)
5. Copy the Access token from the response
6. Use copied Access token to interact with Car Service (mind user roles!)
    ```bash
    curl http://localhost:8081/repairers?sort=id \
        -H "Authorization: Bearer <TOKEN>"
    ```

## How to create a Kubernetes cluster locally?

NOTE: requires Minikube, kubectl, Docker installed

```bash
minikube start --cpus=max --memory=20000Mb --addons=ingress
kubectl apply -f kubernetes/
```

And then you can interact with the Car Service, e.g.:

```bash
curl --resolve "car-service.com:80:$(minikube ip)" http://car-service.com/repairers?sort=id
```

## Tags

Git tags used to mark different stages in projects' lifecycle. You can use them to quickly navigate to and inspect the
state.

* **stage-1** - All required use cases implemented. Unit tests in place. CI configured
* **stage-2** - Application state saved on disk instead of keeping it in memory. Application can be configured using '
  application.toml' file
* **stage-3** - Application can be interacted with through HTTP requests instead of console interface
* **stage-4** - Application state saved in database using JDBC instead of disk.
* **stage-5** - Application state saved in database using JPA instead of JDBC.
* **stage-6** - Application utilizes Spring Boot with starters for HTTP and storage configuration.
* **stage-7** - Application uses PostgreSQL. The GitHub Actions pipeline includes step to build and push car-service
  container image.
* **stage-8** - There are Kubernetes manifests to run the Application in the Kubernetes cluster.
* **stage-9** - Application publishes a message on order creation either to Apache Kafka or
  ActiveMQ Artemis using Spring profiles ("apache-kafka" or "artemis" respectively).