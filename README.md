Expense Tracker -

Team Members-
1.K.ASHISH - se22uari064 2.MADDU YASHWANTH - se22uari187 3.K. VASANTH KUMAR - se22uari076 4.K. HARI PRASAD - se22uari062 5.SRIKAR YARRAMADDA - se22uecm080

Introduction -
The Expense Tracker is a desktop-based application designed to help users manage and track their personal finances. It allows users to record income, expenses, and other financial transactions, categorize them, and generate reports for better financial management. The application is built using Object-Oriented Programming (OOP) principles, ensuring maintainability, scalability, and modular design.

Objective -
The primary objective of this project is to develop a user-friendly and functional expense tracking system that enables users to:

1.Track daily expenses and income. 2.Organize transactions into different categories. 3.View reports and analytics of their spending habits. 4.Ensure secure user authentication and data management. 5.Implement solid object-oriented principles for code maintainability and scalability.

SCOPE -
This project is intended for individual use, allowing users to:

-->Register an account. -->Log in securely. -->Add, edit, and delete income/expense records. -->Categorize transactions (e.g., groceries, entertainment). -->View summaries and transaction histories. The application does not include advanced financial features such as tax calculations or integrations with bank accounts, but it provides a basic framework that could be expanded in the future.

How to Use the Code -
Clone the Repository - git clone https://github.com/VASANTH777999/OOPS_Project cd OOPS_Project
2.Setup the Environment: Ensure you have Java (JDK 8 or above) and any necessary libraries (Swing, JDBC for database integration).

3.Compile the Code: If you are using an IDE like IntelliJ IDEA or Eclipse, you can open the project and run the main() method directly. Alternatively, if you are using the command line: javac Expense_Tracker.java

Run the Application: To start the application, execute the main() method in the ExpenseTracker class. The login window will appear, and you can either log in or register a new user. java Expense_Tracker
Features -
1.User Authentication: Login and Registration functionality with username and password. 2.Expense and Income Management: Add, edit, and delete transactions. Categorize transactions (e.g., groceries, rent, entertainment). 3.Transaction Summary: View a list of all transactions. Filter transactions based on categories. 4.Security: Passwords are securely handled using basic encryption mechanisms (e.g., hashing).

Project Structure - 
ExpenseTracker/
│
├── Expense_Tracker.java
│           ├── UserManager.java            # User Authentication and Registration
│           ├── ExpenseManager.java         # Expense Management Logic
│           ├── Transaction.java            # Transaction model
│           ├── ExpenseTracker.java         # Main Class to Run the App
│           ├── LoginWindow.java            # Login Window UI
│           └── ExpenseCategory.java  
└── README.md

System Architecture-
The application follows a Client-Server architecture. The client-side user interface is built using Java Swing, which provides a graphical interface for user interaction. The server-side logic handles user authentication, transaction management, and database integration.

--> Client: The user interacts with the LoginWindow for authentication. Once authenticated, the ExpenseManager handles the financial transactions. --> Server: The UserManager manages user accounts, including login, registration, and password handling. Transactions are stored in a local database or file system for persistence.

System Modules -
1.Login Module: Handles user login and registration, ensuring secure access. 2.Expense Manager Module: Manages the addition, deletion, and categorization of income/expense records. 3.Database Module: Manages data persistence for transactions and user data. 4.Authentication Module: Handles secure password storage and validation.

Methodology -
The project follows an Agile development methodology with regular iterations and testing phases. Each feature was developed and tested in small increments, ensuring early identification of issues.

Future Enhancements -
---> Advanced Reporting: Generate detailed financial reports such as monthly/annual summaries and pie charts. ---> Data Synchronization: Add cloud sync to allow multiple devices to access and update financial data. ---> Integration with Financial Institutions: Enable integration with bank accounts for automatic transaction imports. ---> Mobile App: Develop a mobile version of the application using Java for Android or Flutter for cross-platform compatibility. ---> Budgeting Feature: Implement a budgeting tool that tracks and compares expenses against a defined budget.

Acknowledgments -
1.Java Development Kit (JDK) for providing the core development environment. 2.Swing for creating the graphical user interface. 3.SQLite or any other database system used for persistence. 4.Special thanks to all the team members for their collaborative effort and dedication in building this application.
