# Firebase Compose

Firebase Compose is an Open Source library for Android that allows you to
quickly connect a Jetpack Compose app to [Firebase](https://firebase.google.com) APIs.

## Table of contents

1. [Usage](#usage)
1. [Installation](#installation)
1. [Dependencies](#dependencies)
   1. [Compatibility](#compatibility-with-firebase-libraries)
1. [Sample App](#sample-app)
1. [Contributing](#contributing)
1. [License](#license)
1. [Acknowledgment](#acknowledgment)

## Usage

Firebase Compose has separate modules for using Firebase Realtime Database and Cloud Firestore.
 To get started, see the individual instructions for each module:

* [Firebase Compose Firestore](firestore/README.md)
* [Firebase Compose Database](database/README.md)

## Installation

Firebase Compose is published as a collection of libraries separated by the
Firebase API they target. Each Firebase Compose library has a transitive
dependency on the appropriate Firebase SDK so there is no need to include
those separately in your app.

**Step 1** - Add the jitpack maven in your root `build.gradle` at the end of repositories:
```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

**Step 2** - In your `app/build.gradle` file add a dependency on one of the Firebase Compose
libraries.

```groovy
dependencies {
    // Firebase Compose for Firebase Realtime Database
    implementation 'com.github.rosariopfernandes.firebasecompose:database:1.0.0-beta01'

    // Firebase Compose for Cloud Firestore
    implementation 'com.github.rosariopfernandes.firebasecompose:firestore:1.0.0-beta01'
}
```

After the project is synchronized, we're ready to start using Firebase functionality in our Compose app.

## Dependencies

### Compatibility with Firebase libraries

Firebase Compose libraries have the following transitive dependencies on the Firebase SDK:
```
firebasecompose-database
|--- com.google.firebase:firebase-database-ktx

firebasecompose-firestore
|--- com.google.firebase:firebase-firestore-ktx
```

## Sample app
A sample app is available in the [demo](/demo/) directory.

## Contributing

Anyone and everyone is welcome to contribute. Please take a moment to
review the [contributing guidelines](CONTRIBUTING.md).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgment
README files inspired by [FirebaseUI](https://github.com/firebase/FirebaseUI-Android/)
