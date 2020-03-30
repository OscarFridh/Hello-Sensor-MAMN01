# Hello-Sensor-MAMN01
MAMN01 Avancerad interaktionsdesign (VT20)

## 1. En länk till koden på GitHub
https://github.com/OscarFridh/Hello-Sensor-MAMN01

## 2. En kortfattad och tydlig beskrivning av hur du byggt din app. Skriv var du kopierat kod ifrån och vad du har modifierat. Lite text med punktlistor funkar bra!

[Build your first app](https://developer.android.com/training/basics/firstapp/index.html)
användes först för att ta reda på hur man bygger ett projekt i Android Studio med vyer, activities och navigering.
Appen är uppbyggd med 3 activities, [MainActivity](app/src/main/java/com/example/myfirstapp/MainActivity.java),
[CompassActivity](app/src/main/java/com/example/myfirstapp/CompassActivity.java) och
[AccelerometersActivity](app/src/main/java/com/example/myfirstapp/AccelerometersActivity.java),
där Main länkar till de två andra via knappar.

### Kompassen
Kod för kompassen kopierades först från
[Compass tutorial](https://www.wlsdevelop.com/index.php/en/blog?option=com_content&view=article&id=38)
För att dölja komplexiteten av att läsa från olika sensorer kapslades den delen av koden in i en egen klass,
[CompassAzimuthReader](app/src/main/java/com/example/myfirstapp/CompassAzimuthReader.java).

#### Filtrering av signalerna

Kod kopierades från [Lågpassfiltrering av sensorer](https://www.built.io/blog/applying-low-pass-filter-to-android-sensor-s-readings).
Några modifikationer gjordes i hur koordinatsystemet mappas för att få det att fungera då telefonen är liggandes.
Mer specifikt ändrades:
```
int rotation = Compatibility.getRotation(this);
if (rotation == 1) {
    SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
} else {
    SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
}
```
till:
```
SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y, rotationMatrix);
```

[Se commit](https://github.com/OscarFridh/Hello-Sensor-MAMN01/commit/3caf2563d5286434a37b1e928277cd7a2fcc57cb)

#### Ändring av bakgrundsfärger

Då kompassen pekar mot norr ändras brakgrundsfärgen till röd. Detta sker gradvis baserat på vinkeln.
Kod för att interpolera mellan två färger kopierades och klistrades in från Mark Renouf's svar på
[Stackoverflow](https://stackoverflow.com/questions/4414673/android-color-between-two-colors-based-on-percentage)

[Se commit](https://github.com/OscarFridh/Hello-Sensor-MAMN01/commit/0590a59c67c5e9b0272676ea5f1ba09999ce97da)

### Accelerometer
Kod utgick ifrån kompass delen, som redan hade skrivits med liknande kod för att läsa av sensorer.
