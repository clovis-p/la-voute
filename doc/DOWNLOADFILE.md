# Téléchargement des fichiers
## Usage
1. Faire GET "/api/files/{id}/download", l'id est l'ID du fichier sélectionné
2. C'est tout, le backend retourne un code 200 OK si ça fonctionne
- ** Content-Disposition: attachment - est automatiquement retourné par le backend et c'est ça qui déclenche le téléchargement côté navigateur