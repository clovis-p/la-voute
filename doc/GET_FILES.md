# GET LES FICHIERS
## Usage
1. Faire Get "/api/files/obtain" et l'endpoint a besoin d'une seule chose dans un form-data :
 - paramètre parentDirId - type int (Non-obligatoire)

Fonctionnement : 
- Lorsque l'utilisateur est à la racine de ses fichiers, tout simplement rien m'envoyer pour get les fichiers qui n'ont aucuns parents (racine)
- Lorsque l'utilisateur ouvre un dossier (répertoire), envoyer le ID du répertoire sur lequel il a cliqué dans la requête pour retourner les fichiers qui sont à l'intérieur

2. Réponses possibles :
 - 200 OK : Les fichiers sont obtenus en format Collection<File>
 - 400 Bad Request : Le plus possible, tu m'as fourni un ID du parent qui n'existe pas en BD (devrait pas arriver normalement), si c'est pas ça y'a une exception adaptée
 - 403 Forbidden : le user est pas authentifié for some reasons