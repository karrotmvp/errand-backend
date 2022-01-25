# 당근 심부름 (Errand)

<img src = "https://user-images.githubusercontent.com/61582017/150923204-48ddf82e-88f6-49f9-bdc2-e5d26c60fc1b.png" height="200px"> <img src = "https://user-images.githubusercontent.com/61582017/150922912-c7aca875-3d66-46bc-a719-4a9d1b767121.png" height="200px">


내 근처 이웃에게 필요한 심부름을 부탁하는 서비스 - Karrot MVP Internship Team 5

서비스 운영 기간: 2021.11.23 ~ 2021.12.24 (종료)

---

# Application Infrastructure Diagram

<img src = "https://user-images.githubusercontent.com/61582017/150918327-7f01b91d-72d8-4a50-8706-cd7a091aafee.png" width="80%">

# Errand API Docs
[📚 Notion Page](https://jazzy-foundation-317.notion.site/API-list-8d4e35b3e5234521b98295010b92db4d)


# Libraries

- `kassava`: kotlin functions for implementing toString(), hashCode() and equals() without all of the boilerplate.
- `mapstruct`: for converting objects
- `querydsl`: for type-safe SQL-like queries for multiple backends including JPA
- `jackson-module-kotlin`: for serialization/deserialization of Kotlin classes and data classes

---

- `jjwt`: generate and decode jwt for each user
- `okhttp3`: for making request for Karrot OAPI
- `springfox-boot-starter`: for generating swagger UI and API docs (openAPI)
- `thymeleaf`: publishing admin static pages

---

- `spring-cloud-starter-aws`: for s3 synchronized client (image upload)
- `awssdk`: for s3 asynchronized client
- `midxpanel-java`: user event tracking
- `dd-trace`: tracing API operation
- `sentry-spring-boot-starter`: tracing application Error/Exceptions

---

- `h2`: mock database for testing
- `mockk`: for mocking in kotlin unit test
