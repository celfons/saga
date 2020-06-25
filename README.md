SAGA'S OPEN SOURCE BY KOTLIN
==============

# Use

- By Maven Repository
- Import GIT

# Dependencies

- Kotlin 1.3.71
- Spring Boot Starter 2.2.6.RELEASE - test scope*

---

## Example

```Kotlin
// code away!

/* Extends Workflow class */
open class WorkflowTest( 
        override var data: Object? = null /* Overridden data type to your context */
        override var status: Object? = null /* Overridden status type to your context */
        var repository: JpaRepository<Object, UUID>? = null /* Added repository to your context */
) : Workflow(data = data, status = status) 
 /* Optional implements rollback error flow and updated status flow  */

@Service /* Build service class and implements business rules */
open class Service: WorkflowService()  

WorkflowTest() /* Execute sync or async and sequentially  */
  .save("INITIAL") /* Optional save workflow progress */
  .flow(service) /* Inject your service  */
  .save("PROCESSING")
  .flow(outherService, true) /* Inject your async service  */
  .save("SUCCESS")
  /* Implements workflow rules */
```

---

## Test Coverage
| Package Class |   Class, %   |      Method, %    |      Line, %       |
| --------------| -------------|-------------------| -------------------|   
|  all classes  |  100% (3/ 3) |  50% (10/ 20)   |   57% (23/ 40)   |

[![Build Status](http://img.shields.io/travis/badges/badgerbadgerbadger.svg?style=flat-square)](https://travis-ci.org/badges/badgerbadgerbadger)

## License
- OPEN SOURCE

## Article PT-BR
- https://www.linkedin.com/pulse/padr%C3%A3o-saga-para-arquitetura-de-microservi%C3%A7os-marcel-fonseca/
