echo "Application is running with local profile."
echo "To stop the application, use Ctrl+C."
echo "You can access the application at http://localhost:8080"
echo "Press any key to stop the application..."
mvn spring-boot:run -Dspring-boot.run.profiles=local
