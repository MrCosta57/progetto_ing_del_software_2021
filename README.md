### Annunci / Status del progetto
Beta version - Codice Sorgente 1.0

Non tutte le features sono testate e funzionanti

# Progetto di Ingegneria del Software 2021

# Indice

- [Come iniziare](#come-iniziare)
- [Configurazione](#configurazione)
- [Inizializzazione](#inizializzazione)
- [Main project](#main-project)

# Come iniziare
## Requisiti
- Android Studio
- NodeJs

## Configurazione
- Conare il repository ```git clone https://github.com/MrCosta57/progetto_ing_del_software_2021/```

- Parte **server**<br/>
collocarsi nella cartella "/server" nella directory principale del repository.
Lanciare da terminale il comando ```npm install``` per installare le varie dipendenze.


- Parte **client**<br/>
aprire mediante Android Studio la cartella "/client" ed attendere l'indexing ed il download dei pacchetti richiesti svolto in automatico dall'IDE.
Dalla schermata iniziale di progetto di Android Studio navigare all'interno delle directory app--> java--> com.backbuffalos.familiesshareextended--> Retrofit--> RetrofitClient e modificare i parametri relativi a indirizzo ip e porta.
Questi parametri sono necessari per il collegamento dell'app Android con il server.
Nel caso si voglia eseguire client e server sullo stesso host, è necessario settare il parametro relativo all'indirizzo ip all'indirizzo della macchina locale. Per visualizzarlo lanciare da terminale il comando ```ipconfig``` (Windows).


## Inizializzazione
Avviare prima il server posizionandosi nella cartella "/server" e lanciare il comando ```npm run start```.
Se il comando è stato eseguito correttamente il terminale restituisce il messaggio:
```
Server started at http://localhost:4000.
Connected to database
```

Avviare poi il client premendo il pulsante "run" in alto a destra di Android Studio


### Main project references
- https://families-share.eu/
- https://www.families-share-toolkit.eu/
- https://github.com/vilabs/Families_Share-platform

### Autori e Copyright
Black Buffalos group - University of Venice Ca' Foscari
