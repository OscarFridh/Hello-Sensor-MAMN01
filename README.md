# Hello-Sensor-MAMN01
MAMN01 Avancerad interaktionsdesign (VT20)

## 1. En länk till koden på GitHub
https://github.com/OscarFridh/Hello-Sensor-MAMN01

## 2. En kortfattad och tydlig beskrivning av hur du byggt din app. Skriv var du kopierat kod ifrån och vad du har modifierat. Lite text med punktlistor funkar bra!

[Build your first app](https://developer.android.com/training/basics/firstapp/index.html) användes först för att ta reda på hur man bygger ett projekt i Android Studio med vyer, activities och navigering.
Appen är uppbyggd med 3 activities, [MainActivity](app/src/main/java/com/example/myfirstapp/MainActivity.java), [CompassActivity](app/src/main/java/com/example/myfirstapp/CompassActivity.java) och [AccelerometersActivity](app/src/main/java/com/example/myfirstapp/AccelerometersActivity.java), där Main länkar till de två andra via knappar.

### Kompassen
Kod för kompassen kopierades först från [Compass tutorial](https://www.wlsdevelop.com/index.php/en/blog?option=com_content&view=article&id=38)
För att dölja komplexiteten av att läsa från olika sensorer kapslades den delen av koden in i en egen klass, [CompassAzimuthReader](app/src/main/java/com/example/myfirstapp/CompassAzimuthReader.java).

Utvidningar:
* Filtrering av accelerometer och magnetometer värden (inspirerat av [Lågpassfiltrering av sensorer](https://www.built.io/blog/applying-low-pass-filter-to-android-sensor-s-readings))
 - dasd
* Ändra bakgrundsfärger när kompassen pekar mot norr (kod från [Mark Renouf's svar på Stackoverflow](https://stackoverflow.com/questions/4414673/android-color-between-two-colors-based-on-percentage))

### Accelerometer
Kod utgick ifrån kompass delen, som redan hade skrivits med liknande kod för att läsa av sensorer.
