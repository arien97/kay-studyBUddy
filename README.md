# StudyBUddy App

## Overview
StudyBUddy is an app designed to help students discover study events, join course communities, and utilize a smart calendar for better study management.

## Features

### Login Screen
- Users are greeted with a login screen upon launching the app.

### Onboarding Screens
- Intuitive onboarding experience with a couple informative screens.
- Introduces key functionalities: discovering study events, joining course communities, and using the smart calendar.
- Horizontal pager with smooth animations, large icons, titles, and detailed descriptions.
- Navigation buttons and page indicators for easy movement between screens.

### Home Screen
- Discovery page for searching study session events.
- Search filters for courses and timeframes.
- Lazy column of event cards displaying event name, course, time, and host.
- Clicking on an event card opens the event details page with more information.
- Event details include name, host, description, time/date, and location.
- Google Maps API used to display event location as a pin on a map.

### Chats Screen
- Comprehensive chat functionality with automatic enrollment in course-specific chat groups.
- Group chats and direct messaging capabilities.
- Friend request management and real-time message synchronization through Firebase.

### Create Event Screen
- Users can create study session events by inputting title, description, time, place, and course.
- Events are sent to the Firebase database.

### Calendar Screen
- Displays a calendar of the current, future, and previous months.
- Users can scroll through months or use arrow buttons for navigation.
- Current day is highlighted, and days with events are marked.
- Clicking on a day shows event details, similar to the Home Screen.
- Users can delete events from their calendar.
- Events can be saved through the Home Screen or Create Event Screen.
- Personal calendars with private events.

### Profile Screen
- Traditional profile screen with username, user picture, and event history.
- Edit icon for changing display name and profile picture.
- Event history includes created and saved events.
- Options to edit or delete created events, and remove saved events from the calendar.
- Logout and account deletion options.

## Usage
1. Launch the app and log in using your BU email.
2. Follow the onboarding screens to learn about the app's features.
3. Use the Home Screen to discover and join study events.
4. Chat with classmates using the Chats Screen.
5. Create new events using the Create Event Screen.
6. Manage your schedule with the Calendar Screen.
7. Update your profile and view event history on the Profile Screen.
