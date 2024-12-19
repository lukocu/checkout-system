# Checkout System

## Overview

The **Checkout System** is a Spring Boot-based application designed to manage products, apply promotions, and handle the checkout process for an e-commerce platform. It provides RESTful APIs for product management, basket operations, and promotion handling, ensuring a seamless and efficient shopping experience for users.

## Features

- **Product Management:** Add, retrieve, and manage products with various pricing strategies.
- **Promotion Engine:** Apply bundle and quantity-based promotions to optimize customer savings.
- **Checkout Process:** Scan products, view applied promotions, and finalize purchases with receipt generation.
- **Comprehensive Testing:** Robust unit and integration tests using MockMvc to ensure reliability.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring MVC**
- **Spring Data JPA**
- **H2 Database** (for development and testing)
- **JUnit 5**
- **MockMvc**
- **Maven**

## Installation

### Prerequisites

- **Java 17** or higher installed. You can download it from [AdoptOpenJDK](https://adoptopenjdk.net/).
- **Maven** installed. Installation instructions can be found [here](https://maven.apache.org/install.html).

### Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/lukocu/checkout-system.git
   cd checkout-system
   ```

## Building the Project

Navigate to the project directory and execute the following Maven command to build the project:

```bash
mvn clean install
```

This command will compile the code, run the tests, and package the application into a JAR file located in the `target/` directory.

## Running the Application

After building the project, you can run the application using the following command:

```bash
mvn spring-boot:run
```

Alternatively, you can execute the generated JAR file:

```bash
java -jar target/checkout-system-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080/`.

## API Endpoints
http://localhost:8080/swagger-ui/index.html
### Product Management

- **Get All Products**

  ```http
  GET /api/v1/products
  ```

- **Get Product by Code**

  ```http
  GET /api/v1/products/{code}
  ```

- **Add a New Product**

  ```http
  POST /api/v1/products
  Content-Type: application/json

  {
    "code": "A",
    "normalPrice": 40.0,
    "specialQuantity": 3,
    "specialPrice": 30.0,
    "description": "Product A"
  }
  ```

### Checkout Operations

- **Scan a Product**

  ```http
  POST /api/v1/checkout/scan
  Content-Type: application/json

  {
    "productCode": "A",
    "quantity": 2
  }
  ```

- **Finalize Checkout**

  ```http
  POST /api/v1/checkout/finalize
  ```

- **View Basket**

  ```http
  GET /api/v1/checkout/basket
  ```

## Testing

The project includes comprehensive tests to ensure functionality and reliability.

### Running Tests

Execute the following Maven command to run all tests:

```bash
mvn test
```

### Test Coverage

- **Unit Tests:** Validate individual components such as services and repositories.
- **Integration Tests:** Ensure that different parts of the application work together as expected using MockMvc.

## Important Classes

- **`ProductController`**: Handles HTTP requests related to product management.
- **`CheckoutController`**: Manages checkout operations, including scanning products and finalizing purchases.
- **`PromotionService`**: Applies promotions based on defined rules and updates the basket accordingly.
- **`ProductService`**: Contains business logic for managing products.
- **`BasketService`**: Manages the user's basket, including adding and removing items.
- **`AppliedPromotionDTO`**: Data Transfer Object representing applied promotions.

## Design Overview

### Architecture

The Checkout System follows a layered architecture, separating concerns across different layers:

1. **Controller Layer:** Handles incoming HTTP requests and maps them to appropriate services.
2. **Service Layer:** Contains business logic for managing products, promotions, and checkout processes.
3. **Repository Layer:** Interacts with the database using Spring Data JPA repositories.
4. **DTOs:** Facilitate data transfer between layers without exposing internal entities.

### Promotions Handling

Promotions are categorized into:

- **Bundle Promotions:** Offer discounts when specific combinations of products are purchased together.
- **Quantity Promotions:** Provide discounts based on the quantity of a single product in the basket.

The `PromotionService` applies these promotions in a defined order, ensuring that the most beneficial discounts are applied without conflicts.

## Rationale

### Technology Choices

- **Spring Boot:** Chosen for its rapid development capabilities and seamless integration with other Spring modules.
- **H2 Database:** Utilized for ease of development and testing without the overhead of setting up external databases.
- **MockMvc & JUnit 5:** Selected for their robust testing capabilities, allowing for both unit and integration tests.

### Design Decisions

- **Layered Architecture:** Ensures a clear separation of concerns, making the application maintainable and scalable.
- **DTO Usage:** Protects internal models from external exposure and facilitates easier data manipulation.
- **Comprehensive Testing:** Guarantees that each component functions correctly and that integrations between components are seamless.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the [MIT License](LICENSE).