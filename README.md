# Geiger 
<a href="https://www.statuscake.com" title="Website Uptime Monitoring"><img src="https://app.statuscake.com/button/index.php?Track=5263662&Days=1&Design=1" /></a>
![Image of Travis status](https://travis-ci.org/lagasild/geiger.svg?branch=master)

Application repository for exam in "DevOps in the Cloud" at Kristiania University College. 
Infrastructure may be found [here](https://github.com/lagasild/infrastructure). 

## About
This exam was initially written under a pseudonym account, @lagasild.
As the exam is over and grading is done, I have transferred the repository
to my own account. In addition, I have changed the commit author to myself.

## Setup

## Local  
* Start influx locally,`./scripts/start_influx.sh`
* Set Spring Boot profile to `local` in IDE
* Start application from `App.kt`
## Pipeline  
* `travis encrypt DOCKER_USERNAME=<your_username> --add`
* `travis encrypt HEROKU_APP_NAME=<your_app_name> --add`
* `travis encrypt $(heroku auth:token) --add deploy.api_key`

## Checklist 
- [X] Endpoints 
    - [X] Posting device 
    - [X] Getting device 
    - [X] Posting measurement
    - [X] Getting measurement
- [X] Branch protection 
- [X] Oppgave 1 - Pipeline
    - [X] Infrastrukturkode i repo 
        - [X] ikke ha modul (ha modul lokalt?) 
        - [X] bruk aws for terraform-state
        - [X] oppdater med travis 
        - [X] Sjekk at det er OK med en lokal terraformmodul
- [X] Travis 
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
- [X] Oppgave 4 - Pipeline
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
- [X] Oppgave 5
    * Denne oppgaven består av å bruke en SAAS tjeneste, Logz.io for 
    innsamling, visualisering og analyse av logger. Dere skal utvide applikasjonen 
    på en slik måte at logger sendes til denne tjenesten. Spring boot applikasjonen
     må modifiseres slik at loggene sendes til tjenesten.
- [X] Sjekk alle TODOs 
- [X] Ga gjennom alt 
      
