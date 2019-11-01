# Geiger ![Image of Travis status](https://travis-ci.org/lagasild/geiger.svg?branch=master)
Main repository for exam in "DevOps in the Cloud" at Kristiania University College. 

## Setup (local/Heroku)
add env variables: 
* LOGZ_URL 
* LOGZ_TOKEN 

## Checklist 
- [X] Endpoints 
    - [X] Posting device 
    - [X] Getting device 
    - [X] Posting measurement
    - [X] Getting measurement
- [X] Branch protection 
- [ ] Oppgave 1 - Pipeline
    - [ ] Infrastrukturkode i repo 
        - [X] ikke ha modul (ha modul lokalt?) 
        - [X] bruk aws for terraform-state
        - [X] oppdater med travis 
        - [ ] Sjekk at det er OK med en lokal terraformmodul
- [ ] Travis 
    - [X] Det skal lages en CI/CDpipeline for applikasjonen ved hjelp av Travis CI.
    - [X] Det skal også være en tilsvarende pipeline for infrastruktur.
    - [X] Pipeline skal deploye hver commit på master branch til "CI" miljøet i heroku, ved ok bygg og når testene ikke feiler.
    - [X] Deployment fra CI-miljø videre til Stage og produksjon skal skje manuelt ved at man promoterer applikasjonen i Heroku UI (Eller CLI). Studentene kan fritt velge å implementre kontinuerlig deployment til stage, og fra stage til prod - men det gis ikke poeng for dette.
- [X] Oppgave 2 - Pipeline
    - [X] Du skal skrive en Dockerfil som kan brukes for å bygge et Container Image av Spring Boot applikasjonen din.
    - [X] Du skal utvide pipeline, til å bygge et Docker image fra Docker filen
    - [X] Docker image skal lastes opp til Docker Hub
- [X] Oppgave 3 - Pipeline
    - [X] Implementer infrastruktur for varsling ved hjelp av SAAS tjenesten OpsGenie
    - [X] Implementer infrastruktur for overvåkning ved hjelp av SAAS tjenesten StatusCake
- [ ] Oppgave 4 - Pipeline
     - [X] logge datapunkter ved hjelp av rammeverket Micrometer
     - [X] Minimun 
        - [X] Gauge
        - [X] Counter
        - [X] Distributionsummary 
        - [X] Timer 
        - [X] LongTaskTimer 
    - [X] Sjekk at forventet data er fornuftig 
    - [X] Annen metric (i og med at kravet er minimum de over) (gaugeCollectionSize)
    - [X] Levere Metrics til InfluxDB (som antasa aa kjoere paa egen container)
    - [X] Dere må bruke Spring profiles og @Configuration i Spring til i bruke SimpleMeterRegistry når applikasjonen ikke kjører lokalt.   
- [ ] Oppgave 5
    * Denne oppgaven består av å bruke en SAAS tjeneste, Logz.io for 
    innsamling, visualisering og analyse av logger. Dere skal utvide applikasjonen 
    på en slik måte at logger sendes til denne tjenesten. Spring boot applikasjonen
     må modifiseres slik at loggene sendes til tjenesten.
- [ ] Sjekk alle TODOs 
- [ ] Ga gjennom alt 
      

## Notes 
* [Maintail two github accounts](https://medium.com/the-andela-way/a-practical-guide-to-managing-multiple-github-accounts-8e7970c8fd46)
* When cloning, use SSH: `git clone git@github.com-devops:lagasild/REPO`
* Mail: lagasild@protonmail.com
* Docker username: lagasild
* Heroku username: mail