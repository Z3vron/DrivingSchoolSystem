http://localhost:8080/health checks if application is running locally(OK if running correctly)

Application user microservice - returns all users
http://localhost:8080/api/users

http://localhost:8081/health checks if application is running in docker (OK if running correctly)

Application user microservice- returns all users
http://localhost:8081/api/users

To run Docker run docker compose up --build

H2 console for DataBase: http://localhost:8080/h2-console

In dataBase: id, firstName, lastName, email, password, role 

In dataBase role: TRAINEE, INSTRUCTOR, ADMIN

adds data 
curl -X POST -H "Content-Type: application/json" -d '{"firstName":"Imie1","lastName":"Nazwisko1","email":"email1@example.com","role":"TRAINEE"}' http://localhost:8081/api/users