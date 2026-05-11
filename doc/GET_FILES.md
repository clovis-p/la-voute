# GET LES FICHIERS
## Usage
1. Faire Get "/api/files/obtain" et l'endpoint a besoin d'une seule chose dans un form-data :
 - paramètre parentDirId - type int (Non-obligatoire)

Fonctionnement : 
- Lorsque l'utilisateur est à la racine de ses fichiers, tout simplement rien m'envoyer pour get les fichiers qui n'ont aucuns parents (racine)
- Lorsque l'utilisateur ouvre un dossier (répertoire), envoyer le ID du répertoire sur lequel il a cliqué dans la requête pour retourner les fichiers qui sont à l'intérieur

2. Réponses possibles :
 - 200 OK : Les fichiers sont obtenus en format Collection<FileGetDTO>
 - 400 Bad Request : Le plus possible, tu m'as fourni un ID du parent qui n'existe pas en BD (devrait pas arriver normalement), si c'est pas ça y'a une exception adaptée
 - 403 Forbidden : le user est pas authentifié for some reasons

3. Exemple type d'une réponse normale si je reçois par exemple, 4 comme parentDirId:

```json
[
  {
    "createdOn": "2026-04-30",
    "id": 5,
    "isDirectory": false, // ici tu peux savoir si c'est un directory ou non
    "name": "Capture d’écran 2026-04-06 190918.png", //Pour avoir le nom et l'extension / mettre une icône adaptée comme tu veux
    "parentDirId": 4,
    "parentDirName": "Dossier", //Si jamais tu veux faire une interface un peu comme sur l'explorateur de fichier windows que tu peux voir le nom du parent en haut
    "size": 463020, //La size est en byte
    "username": "miadionn" //Je sais pas trop si c'était nécessaire que je donne le username mais je l'ai mis comme ça pour l'instant
  },
  {
    "id": 6,
    "isDirectory": true,
    "name": "DossierDans4",
    "parentDirId": 4,
    "parentDirName": "Dossier",
    "username": "miadionn"
  }
]
```