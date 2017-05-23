# Weather-Forecast-App-4th-year-project

Technologies used: Java, XML, JSON, openweather API.

This project requires to design and develop a Rainfall Notification service for Android platform devices. 
It allows the user to configure the settings such as the location (option for current location), the number of days
(in the range between 1 and 16) included in the forecast, and the threshold of rain that triggers a notification.

An Alarm Manager is used to set the time of an alarm to go off, start an Intent Service to download and parse data 
(JSON format), so the notification may be sent if the minimum threshold is satisfied.
