# UPLOAD DE FICHIERS
## Usage
1. Faire POST "/api/files/upload" et l'endpoint a besoin de 2 choses dans un form-data :
 - paramètre file - type File (Obligatoire)
 - paramètre parentDirId - type int (Non-Obligatoire)

Le paramètre parentDirId permet de savoir si le fichier est à la racine ou si il a un répertoire parent

2. Réponses possibles :
 - 202 Accepted : Fichier uploadé avec succès
 - 400 Bad request : Il y a eu une erreur, soit des paramètres, soit du système for some reasons (ex : le fichier ne veut pas s'enregistrer dans /storage)
 - 403 Forbidden : Non authentifié, mais normalement ça c'est chill si t'as accès à l'upload au frontend lol

# CRÉATION D'UN RÉPERTOIRE (Directories)
## Usage
1. Faire POST "/api/files/directory" et l'endpoint a besoin de 2 choses dans un JSON :
```json
{
  "directoryName": "DirectoryName", //String
  "parentDirId": 0 //Integer, null si c'est à la racine
}
```

Le paramètre parentDirId permet de savoir si le répertoire est à la racine ou si il a un répertoire parent

2. Réponses possibles : 
 - 202 Accepted : Fichier uploadé avec succès
 - 400 Bad request : Il y a eu une erreur, most likely de parentDirId, l'id donné donne à un File null ou à un dossier et non un répertoire
 - 403 Forbidden : Comme pour l'upload de fichier, non-authentifié mais normalement y devrait pas avoir de problèmes si t'as bien fait le login



