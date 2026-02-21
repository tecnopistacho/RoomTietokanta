# 🌦️ Weather App + Room

Android-sovellus, joka hakee säätiedot API:sta ja tallentaa ne paikallisesti Room-tietokantaan.  
Sovelluksessa on 30 minuutin välimuisti tehokkuuden parantamiseksi.

---

## 🧱 Mitä Room tekee?  
*(Entity – DAO – Database – Repository – ViewModel – UI)*

Sovelluksessa käytetään **Room-tietokantaa** paikallisen tiedon tallentamiseen.

- 📦 **WeatherEntity**  
  Määrittelee, mitä tietoa tietokantaan tallennetaan  
  (kaupungin nimi, lämpötila, kuvaus ja aikaleima).

- 🗂️ **DAO (Data Access Object)**  
  Sisältää metodit, joilla tietokannasta haetaan ja tallennetaan tietoa.

- 🗄️ **Database (AppDatabase)**  
  Yhdistää Entityn ja DAO:n. Vastaa tietokannan luomisesta ja käytöstä.

- 🔄 **WeatherRepository**  
  Hakee tietoa tietokannasta ja tallentaa API:sta saadun datan Roomiin.  
  Toimii välikerroksena ViewModelin ja datalähteiden välillä.

- 🧠 **ViewModel**  
  Huolehtii sovelluksen logiikasta ja tilanhallinnasta.

- 🎨 **UI (Jetpack Compose)**  
  Näyttää tiedot käyttäjälle.  
  **WeatherScreen** seuraa ViewModelin tilaa ja näyttää:
  - 🌤️ säätiedot  
  - ⏳ latausanimaation  
  - ❌ virheilmoituksen  

---

## 📁 Projektin rakenne
```
data/
    model/        → WeatherEntity, WeatherResponse
    local/        → WeatherDao, AppDatabase
    repository/   → WeatherRepository
    remote/       → RetrofitInstance, WeatherApi


ui/
    WeatherScreen

viewmodel/
    WeatherViewModel
MainActivity
```

---

## 🔄 Miten datavirta kulkee?

1. 📝 Käyttäjä kirjoittaa kaupungin nimen ja painaa **Check Weather** -painiketta.
2. 📲 UI pyytää ViewModelia hakemaan säätiedot.
3. 🗄️ ViewModel tarkistaa ensin, löytyykö tieto Room-tietokannasta.
4. ⏱️ Jos tieto on alle 30 minuuttia vanha → se näytetään suoraan käyttäjälle.  
   🌍 Jos tietoa ei ole tai se on yli 30 minuuttia vanhaa → tehdään uusi API-haku.
5. 💾 API:sta saatu tieto tallennetaan Roomiin.
6. 🔁 Kun tieto päivittyy, myös käyttöliittymä päivittyy automaattisesti.

### 📌 Datavirran yhteenveto

UI → ViewModel → Repository → Room/API → ViewModel → UI

---
## ⏳ Välimuistilogiikka (30 min cache)

Sovelluksessa on toteutettu 30 minuutin välimuisti.

ViewModel tarkistaa ehdon:
now - timestamp < 30 minuuttia
- ✅ Jos ehto täyttyy → käytetään Roomiin tallennettua dataa.
- 🔄 Jos data on vanhempaa kuin 30 minuuttia → tehdään uusi API-kutsu.

Uusi data tallennetaan Roomiin, ja UI päivittyy automaattisesti.  
Näin sovellus toimii tehokkaammin ja API-kutsuja tehdään vähemmän 🚀

---

## 🎥 Demo Video

*(Lisää linkki tähän)*


