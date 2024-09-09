# Android PostgreSQL Viewer

tyoPGView is a mobile application that allows you to connect to a PostgreSQL database and perform various operations such as viewing tables, modifying data, and running queries. 
A convenient way to interact with your PostgreSQL database directly from your Android device.


<img src="https://github.com/user-attachments/assets/e0b5beae-deb8-47e0-aefc-ccf9f094ef15" height="500">

![pgView2](https://github.com/user-attachments/assets/3cbeb889-7bf4-40ee-ab9d-efbf7caac668)
![Screenshot_20240909_214844](https://github.com/user-attachments/assets/5f98c2ed-ccb7-4fe3-b69e-72290221c4e7)

## Features

- Connect to a PostgreSQL database using host, port, username, password, and database name
- View a list of available databases and select a database to work with
- Display tables in the selected database
- View table contents
- Modify table data by clicking on cells and updating values
- Add new rows to tables
- Delete rows from tables
- Export table data to CSV and PDF files
- Run custom SQL queries using the query tool

## Technologies Used

- Kotlin
- Android Jetpack
  - ViewModel
  - LiveData
- PostgreSQL JDBC Driver: For communication with the PostgreSQL database
- RecyclerView
- TableView
- Moshi
