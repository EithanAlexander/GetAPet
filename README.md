# GetAPet

## Description
This project is meant to create an application that helps all sort of pets to find a home.  
It aimed at creating a comprehensive application dedicated to helping pets of all kinds find loving homes.
The application serves as a platform connecting potential pet adopters with pets in need,
ensuring pets has the opportunity to be adopted into a caring family


## Features
- User Registration and Authentication: Users can create accounts, log in, and manage their profiles securely
- Create ads: Shelters and individual pet owners can list pets available for adoption, including detailed information and photos
- Delete ads: once a pet has been adopted the publisher can delete the ad
- Favorite Pets: Users can save ads to their profiles for easy access later
- Guides: Helpful articles for pet owners  

## Technologies Used
- Frontend: React for building a responsive and interactive user interface
- Backend: Java (Spring Boot) for robust and scalable server-side logic
- Database: PostgreSQL for storing user and pet information

## Prerequisites
- Node.js, if you don't have it installed than please install it from [here](https://nodejs.org/en/download/package-manager)
- Java (JDK 17) if you don't have it installed than please install it from [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- PostgreSQL if you don't have it installed than please install it from [here](https://www.postgresql.org/ftp/source/v9.6.23/)


## Configuration

### Frontend
1. Configuration files are located in the src/config directory.

### Backend
1. Resources files are located in the src/main/resources directory.
2. Update application.properties with your database credentials.

## Getting Started

### Frontend
1. Navigate to the frontend directory
   ```bash
   cd WebApp/src/main/webapp/app
2. Install dependencies
   ```bash
   npm install
3. Start the server application
   ```bash
   npm start
##### To stop the application use Ctrl+C and then Y


### Backend
1. Navigate to the backend directory
   ```bash
   cd GetAPetServerSide
2. Run maven clean and install
   ```bash
   ./mvnw clean install
3. Run the application
   ```bash
   ./mvnw spring-boot:run

4. If it fails because of the JAVA_HOME variable, [use this guide to resolve](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html)

##### To stop the application use Ctrl+C and then Y


## Authors 🐶🐱
This project was created by
[Almog Fishel](https://github.com/almogfishel)
and
[Eithan Lavi](https://github.com/EithanAlexander)

We chose this topic for our web app because of our mutual love for animals and the joy we experience raising our adorable,
chubby cat named Aslan. We believe that more people owning pets can bring happiness and improve the lives of both pets
and their owners 

