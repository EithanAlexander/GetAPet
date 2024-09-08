//if port is modified it requires modification in the backend application.properties of variable server.port
const BASE_URL = "http://localhost:8080/api";

//Matches the categories in the DB, should be updated in case of a change
const CATEGORIES = ["Dog", "Cat", "Bird", "Duck", "Reptiles", "Fish", "Rabbit", "Guinea Pigs", "Hamster", "Gerbil", "Ferret", "Hedgehog", "Pig", "Horse", "Ostrich", "Turtle"];

//Configure to the desired amount of ads per page
const ADS_PER_PAGE = [1, 5, 10, 20, 30, 40]

export { BASE_URL, CATEGORIES, ADS_PER_PAGE };
