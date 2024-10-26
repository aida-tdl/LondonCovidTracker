London COVID-19 Statistics Application

Project Overview:

The London COVID-19 Statistics Application is an interactive tool designed to visualize and analyze the impact of the COVID-19 pandemic across the boroughs of London. Using various data panels, including maps, statistics, and graphs, the application provides a comprehensive view of pandemic-related data. Users can explore COVID-19 trends, death rates, mobility changes, and other relevant statistics in an intuitive and user-friendly interface.

Key Features:

	• Interactive Map Panel: A polygon-based map of London boroughs, color-coded based on COVID-19 death rates. Users can click on specific boroughs to view detailed statistics, including new cases, deaths, and mobility changes.
	• Statistics Panel: Displays key COVID-19 statistics, such as total deaths and mobility changes, with an easy-to-navigate slider for various metrics. The statistics update dynamically based on the selected date range.
	• Graph Panel: Visualizes trends in COVID-19 cases, deaths, and mobility over time using customizable graphs. Users can select specific boroughs and data types, and even display boroughs on an external map with the “Show on Map” button.
	• Date Range Selector: Available across all panels, allowing users to specify start and end dates for viewing data. Invalid date ranges prompt an error message to guide users.

Technologies Used:

	• Java: Core programming language for the GUI and backend data handling.
	• Java Swing: For building the graphical user interface (GUI).
	• JUnit: Used for unit testing, ensuring the accuracy of statistics calculations and panel functionality.

Panels:

	• Welcome Panel: Greets users and allows them to select a date range. It also validates user input, ensuring that the selected date range is valid.
	• Map Panel: Displays an interactive map of London’s boroughs, color-coded by death rates. Users can click on boroughs to get detailed data, and the map dynamically updates based on the selected date range.
	• Statistics Panel: Shows various sets of COVID-19 data, including mobility changes and total deaths. The statistics update based on user-selected dates.
	• Graph Panel: Offers visual representation of COVID-19 trends, including cases and deaths over time. The graph can be customized by borough and data type.

Unit Testing:

	• The StatisticsPanelTest class was created to ensure the correctness of the COVID-19 statistics. It verifies that the panel calculates and displays statistics accurately, even with incomplete or missing data. Mock data is used to simulate the real dataset and validate the correctness of calculations.
