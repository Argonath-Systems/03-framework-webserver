# WebServer Framework Build Commands

# Build the framework
build:
    mvn clean install

# Run tests
test:
    mvn test

# Run tests with coverage
test-coverage:
    mvn clean test jacoco:report

# Clean build artifacts
clean:
    mvn clean

# Format code
format:
    mvn spotless:apply

# Check code style
check:
    mvn spotless:check

# Generate documentation
docs:
    mvn javadoc:javadoc
