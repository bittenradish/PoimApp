# PoimApp
App showing different types of POI (Points of Interest) using Google Map Compose SDK

## ❗Before build:
1. Do not forget to put the **Google Map API key** into your `secrets.properties` file.  
2. Provide the correct path to the **Android SDK** in the `local.properties` file.

```
💡 For test purposes, the APK file is available in the Assets folder.
```

## Implemented so far:
- Separated Poi map feature module:
    - Clean arch. + MVVM/MVI
    - Optimized requests through bounding box + cancellable paging flow
    - Achieved smooth map behavior through deadZone approach
- PoiDetails screen:
    - In addition to POIs' details, can display details for the whole Cluster (closely located POIs)
- Landscape orientation support
- Dark/Light theme
- PoiMapViewModel Integration Tests
- MapStateReducer Unit Test

## Screenshots
![map screen](Assets/sc1.png?raw=true "Starting map screen")

![big clusters](Assets/sc2.png?raw=true "Bigger clusters")

![Light theme](Assets/sc3.png?raw=true "Light theme")

![Cluster details](Assets/sc4.png?raw=true "Cluster details")

![Cluster landscape](Assets/sc5.png?raw=true "Cluster landscape")

![Cluster item details](Assets/sc6.png?raw=true "Cluster item details")

![Pokemon details](Assets/sc7.png?raw=true "Pokemon details")

![Single Poi landscape details](Assets/sc8.png?raw=true "Single Poi landscape details")

![Error snackbar](Assets/sc9.png?raw=true "Error snackbar")

![No internet connection](Assets/sc10.png?raw=true "No internet connection")

![Details screen error state](Assets/sc11.png?raw=true "Details screen error state")

