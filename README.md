
# Calendar Api

### Technologies used
- **Java 11 - MongoDB - Swagger - Lombok - Spring boot - Spring Web**

### What the application does
Rest API available for creating and managing interview schedules.

### What you'll need to run and test it
- Postman
- Docker [How to install Docker on Windows](https://docs.docker.com/dektop/windows/install/)
- docker-compose - *Docker Desktop for Windows includes Compose along with other Docker apps*
- Java SDK 11 Configured in the Windows Variables - [How to install on Windows](https://mkyong.com/java/how-to-set-java_home-on-windows-10/)
- Maven configured in the Windows system variables - [How to install on Windows](https://mkyong.com/maven/how-to-install-maven-in-windows/)

### How to run the project
- Clone this project
- After cloned, navigate to the project root folder
- Run the script: **start.sh**

### How to test it in the Postman
1. Open Postman application
2. Import the collection with name *Calendar API.postman_collection.json*
3. **<ins>Important: Set the delay from zero to 50ms in the Runner Tab</ins>**
4. Click on *Run Calendar Api* button

### How the collection works
The collection will create two interviewers and two candidates
* Interviewers:
  - James
    - Availability:  [09:00 - 10:00] [10:00 - 11:00] [11:00 - 12:00] on 2022-12-10
  - Anne
    - Availability:  [13:00 - 14:00] [14:00 - 15:00] [15:00 - 16:00] [16:00 - 17:00] [17:00 - 18:00] on 2022-12-10

* Candidates:
  - Emilly
    - Availability:  [10:00 - 11:00] [11:00 - 12:00] on 2022-12-10
  - Jhonatan
    - Availability:  [15:00 - 16:00] [16:00 - 17:00] [17:00 - 18:00] on 2022-12-10

When the API */candidates/{candidateId}/availabilities/interviewers* is called will returns:

* Candidates:
  - Emilly
    - Interviewer Available: Jame with slots [10:00 - 11:00] [11:00 - 12:00] on 2022-12-10
  - Jhonatan
    - Interviewer Available: Anne with slots [15:00 - 16:00] [16:00 - 17:00] [17:00 - 18:00] on 2022-12-10
    
    
**>> You can check more endpoints accessing http://localhost:8080/swagger-ui.html <<**
