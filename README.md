# Restaurant Management Application

A comprehensive mobile application for managing restaurant operations with separate interfaces for staff and guests. Built with Jetpack Compose and following modern Android development practices.

## Features

### Staff Side
- **Menu Management**: Add, edit, and delete menu items with images, prices, and categories
- **Reservation Management**: View and cancel customer reservations
- **Notifications**: Receive alerts for new reservations and changes
- **Settings**: Customize notification preferences

### Guest Side
- **Menu Browsing**: Browse available menu items with filtering by category
- **Reservations**: Make, edit, and cancel table reservations
- **Notifications**: Get notified when reservations are modified or cancelled
- **Settings**: Manage notification preferences

## Technical Stack

### Architecture
- **MVVM (Model-View-ViewModel)** architecture pattern
- **Repository Pattern** for data management
- **Jetpack Compose** for modern declarative UI
- **Kotlin Coroutines** for asynchronous programming
- **StateFlow** for reactive state management

### Libraries & Dependencies
- **Room Database**: Local SQLite database for offline-first approach
- **Retrofit**: RESTful API integration for server sync
- **Jetpack Compose**: Modern UI toolkit
- **Material 3**: Latest Material Design components
- **DataStore**: User preferences management
- **Coil**: Image loading and caching
- **Navigation Compose**: Type-safe navigation
- **Coroutines**: Asynchronous operations

### Database
- **Room** - Local SQLite database
- Tables: Users, MenuItems, Reservations
- Type converters for enums
- Foreign key relationships

### API Integration
- RESTful API endpoints for:
  - User authentication and registration
  - Menu item CRUD operations
  - Reservation management
- Automatic sync with local database
- Offline-first architecture

## Project Structure

```
com.restaurant.management/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt
│   │   ├── UserDao.kt
│   │   ├── MenuItemDao.kt
│   │   └── ReservationDao.kt
│   ├── model/
│   │   ├── User.kt
│   │   ├── MenuItem.kt
│   │   └── Reservation.kt
│   ├── remote/
│   │   ├── ApiClient.kt
│   │   └── RestaurantApiService.kt
│   └── repository/
│       ├── UserRepository.kt
│       ├── MenuRepository.kt
│       └── ReservationRepository.kt
├── notification/
│   └── NotificationHelper.kt
├── ui/
│   ├── auth/
│   │   ├── LoginScreen.kt
│   │   └── RegisterScreen.kt
│   ├── staff/
│   │   ├── StaffHomeScreen.kt
│   │   ├── StaffMenuScreen.kt
│   │   ├── StaffReservationsScreen.kt
│   │   └── StaffSettingsScreen.kt
│   ├── guest/
│   │   ├── GuestHomeScreen.kt
│   │   ├── GuestMenuScreen.kt
│   │   ├── GuestReservationsScreen.kt
│   │   └── GuestSettingsScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── MainActivity.kt
│   └── RestaurantApp.kt
├── util/
│   └── PreferencesManager.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── MenuViewModel.kt
│   └── ReservationViewModel.kt
└── RestaurantApplication.kt
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17 or higher
- Android SDK with API level 24 or higher
- Kotlin 1.9.20 or higher

### Installation

1. Clone the repository
```bash
git clone <your-repository-url>
cd res_management
```

2. Open the project in Android Studio
```bash
# Option 1: Using command line (macOS)
open -a "Android Studio" .

# Option 2: Open Android Studio and select "Open Project"
```

3. Sync Gradle files
   - Android Studio should automatically prompt to sync
   - Or click: File → Sync Project with Gradle Files

4. Run the application
   - Select a device/emulator
   - Click the Run button or press Shift+F10

### Configuration

#### API Endpoint
Update the base URL in `ApiClient.kt`:
```kotlin
private const val BASE_URL = "https://your-api-endpoint.com/"
```

#### Database
The local database is automatically created on first launch. No additional configuration needed.

## Usage

### First Time Setup

1. **Launch the app**
2. **Create an account**:
   - Tap "Sign Up"
   - Enter your details
   - Select role (Staff or Guest)
3. **Login** with your credentials

### Staff Features

#### Managing Menu Items
1. Navigate to Menu tab
2. Tap the + button to add new items
3. Fill in details: name, price, description, category, image URL
4. Edit/Delete items using the action buttons

#### Managing Reservations
1. Navigate to Reservations tab
2. View all customer reservations
3. Cancel reservations if needed
4. Receive notifications for new bookings

### Guest Features

#### Browsing Menu
1. Navigate to Menu tab
2. Filter by category using chips
3. View item details, prices, and images

#### Making Reservations
1. Navigate to Reservations tab
2. Tap the + button
3. Enter date, time, table number, number of guests
4. Add special requests (optional)
5. Submit reservation

#### Managing Reservations
- Edit existing reservations
- Cancel reservations
- Receive notifications for changes

## Notification System

The app includes a comprehensive notification system:

- **Staff Notifications**:
  - New reservation alerts
  - Reservation modification alerts

- **Guest Notifications**:
  - Reservation confirmation
  - Modification notifications
  - Cancellation alerts

- **Customization**:
  - Enable/disable all notifications
  - Granular control per notification type
  - Settings accessible from Settings tab

## Database Schema

### Users Table
- id (Long, Primary Key)
- email (String)
- password (String)
- name (String)
- role (UserRole enum)
- notification preferences (Boolean flags)
- timestamps

### MenuItems Table
- id (Long, Primary Key)
- name (String)
- description (String)
- price (Double)
- imageUrl (String)
- category (String)
- available (Boolean)
- timestamps

### Reservations Table
- id (Long, Primary Key)
- userId (Long, Foreign Key)
- userName (String)
- userEmail (String)
- tableNumber (Int)
- numberOfGuests (Int)
- reservationDate (String)
- reservationTime (String)
- status (ReservationStatus enum)
- specialRequests (String)
- timestamps

## API Endpoints

### User Endpoints
- POST `/api/users/register` - Register new user
- POST `/api/users/login` - User login
- GET `/api/users/{id}` - Get user by ID
- PUT `/api/users/{id}` - Update user

### Menu Endpoints
- GET `/api/menu` - Get all menu items
- GET `/api/menu/{id}` - Get menu item by ID
- POST `/api/menu` - Create menu item (Staff only)
- PUT `/api/menu/{id}` - Update menu item (Staff only)
- DELETE `/api/menu/{id}` - Delete menu item (Staff only)

### Reservation Endpoints
- GET `/api/reservations` - Get all reservations (Staff only)
- GET `/api/reservations/user/{userId}` - Get user reservations
- GET `/api/reservations/{id}` - Get reservation by ID
- POST `/api/reservations` - Create reservation
- PUT `/api/reservations/{id}` - Update reservation
- DELETE `/api/reservations/{id}` - Cancel reservation

## Development

### Building the Project
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

### Code Style
The project follows Kotlin coding conventions and Android best practices:
- MVVM architecture
- Repository pattern
- Dependency injection via Application class
- Reactive programming with Kotlin Flows
- Compose UI best practices

## Responsive Design

The app is designed to work across different screen sizes and orientations:
- Phone (portrait and landscape)
- Tablet layouts
- Dynamic orientation handling
- Adaptive Material 3 components

## Security

- Password encryption (implement in production)
- Secure API communication (HTTPS)
- Network security configuration
- Data encryption at rest (Room)
- Proper permission handling

## Future Enhancements

- [ ] Push notifications via FCM
- [ ] Image upload for menu items
- [ ] Payment integration
- [ ] Order management
- [ ] Analytics dashboard
- [ ] Multi-language support
- [ ] Dark mode enhancements
- [ ] Table availability calendar
- [ ] Customer reviews and ratings

## Troubleshooting

### Common Issues

**Build Errors**
- Clean project: Build → Clean Project
- Invalidate caches: File → Invalidate Caches / Restart
- Check Gradle version compatibility

**Database Issues**
- App data: Settings → Apps → Restaurant Management → Clear Data
- Or use fallbackToDestructiveMigration (already configured)

**API Connection Issues**
- Check network permissions in manifest
- Verify API endpoint URL
- Check network security config for localhost

## License

This project is for educational purposes as part of a coursework assignment.

## Contact

For questions or support regarding this coursework project, please contact your instructor.

## Acknowledgments

- Material Design 3 guidelines
- Android Jetpack documentation
- Kotlin coroutines documentation
- Retrofit documentation
- Room persistence library
