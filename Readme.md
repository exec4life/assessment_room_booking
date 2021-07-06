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
       3.1.1 Create a new booking
        + URL: post: localhost:8000/api/booking
       3.1.2 Edit the booking
        + URL: put: localhost:8000/api/booking/{booking id}
       3.1.3 Get list booking of a room
        + URL: get: localhost:8000/api/booking/list/{room id}
       3.1.4 Get one booking
        + URL: get: localhost:8000/api/booking/{booking id}
       3.1.5 Search booking
        + URL: post: localhost:8000/api/booking/search
       3.1.6 Delete booking
        + URL: delete: localhost:8000/api/booking/{booking id}
   3.2 Room APIs
       3.2.1 Create new 1 room
        + URL: post: localhost:8000/api/room
       3.2.2 Edit room:
        + URL: put: localhost:8000/api/booking/{room id}
       3.2.3: Get one room
        + URL: get: localhost:8000/api/room/{room id}
       3.2.4: Get all room
        + URL: localhost:8000/api/room/list
       3.2.4 Archive room
        + URL: localhost:8000/api/room/{room id}
   3.3 User APIs
       3.3.1 Get user list
        + URL: localhost:8000/api/user/list
