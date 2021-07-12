** User company **
_Assessment Room Booking_

1. Framework/Libraries/Technologies 
   1.1 Spring boot: 2.4.5
   1.2 ORM: JPA
   1.3 Database: h2 (in-memory database)
   1.4 Recurrence Rule: lib-recur:0.12.2
   1.5 Technologies stack
    + REST APIs controller
    + JPA: @uery, camelcase
    + Exception handling
    + Fields validation
    + Message source with localization
    + Logging
    + Integration test
    + Environment separation
   
2. Testing tool
   2.1 Postman: import file [postman/AssessmentRoomBooking.postman_collection.json] to run test-cases
   
3. APIs detail
   
   3.1 Booking APIs
   - Create a new schedule
        + URL: post: localhost:8000/api/schedule
   - Edit the schedule
        + URL: put: localhost:8000/api/schedule/{schedule id}
   - Get list schedule of a room
        + URL: get: localhost:8000/api/schedule/list/{room id}
   - Get one schedule
        + URL: get: localhost:8000/api/schedule/{schedule id}
   - Search schedule
        + URL: post: localhost:8000/api/schedule/search
   - Delete schedule
        + URL: delete: localhost:8000/api/schedule/{schedule id}
    
   3.2 Room APIs
   - Create new 1 room
        + URL: post: localhost:8000/api/room
   - Edit room:
        + URL: put: localhost:8000/api/schedule/{room id}
   - Get one room
        + URL: get: localhost:8000/api/room/{room id}
   - Get all room
        + URL: localhost:8000/api/room/list
   - Archive room
        + URL: localhost:8000/api/room/{room id}
    
   3.3 User APIs
   - Get user list
        + URL: localhost:8000/api/user/list
