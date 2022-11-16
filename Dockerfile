FROM openjdk:11
COPY target/calendarApi.jar /home/calendarApi.jar
ENTRYPOINT ["sh","-c","java -jar /home/calendarApi.jar com.gabryellr.calendarapi.CalendarApiApplication.class"]