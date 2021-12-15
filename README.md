## Annunci / Status del progetto
Beta version - Codice Sorgente 1.0
Non tutte le features sono testate e funzionanti

# Progetto di Ingegneria del Software 2021

# Indice

- [Come iniziare](#come-iniziare)
- [Configurazione](#configurazione)
- [Inizializzazione](#inizializzazione)
- [Main project](#main-project)

# Come iniziare
## Dipendenze
Android Studio
NodeJs

## Configurazione
Conare il repository ```git clone https://github.com/MrCosta57/progetto_ing_del_software_2021/```.
Per installare la parte **server** collocarsi nella cartella /server nella directory pricipale del repository.
Lanciare da terminale il comando ```npm install``` per installare le varie dipendenze.

Per installare la parte **client** aprire mediante Android Studio la cartella /client ed attendere l'indexing ed il download dei pacchetti richiesti svolto in automatico dall'IDE.
Dalla schermata iniziale di progetto di Android Studio navigare all'interno delle directory app-->java-->com.backbuffalos.familiesshareextended-->Retrofit-->RetrofitClient e modificare i parametri relativi a indirizzo ip e porta.
Questi parametri sono necessari per il collegamento dell'app Android con il server.
Nel questo si voglia eseguire client e server sullo stesso host, è necessario settare il parametro relativo all'indirizzo ip all'indirizzo della macchina locale. Per visualizzarlo lanciare da terminale il comando ```ipconfig ``` (Windows).


## Inizializzazione
Per avviare il server posizionarsi nella cartella /server e lanciare il comando ```npm run start```.
Se il comando è stato eseguito correttamente il terminale dovrebbe restituire il messaggio:
```
Server started at http://localhost:4000.
Connected to database
```

Per avviare il client invece basta premere il pulsante "play" in alto a destra di Android Studio


## Main project
https://families-share.eu/
https://www.families-share-toolkit.eu/
https://github.com/vilabs/Families_Share-platform

## Autori e Copyright
Black Buffalos group - University of Venice Ca'Foscari
