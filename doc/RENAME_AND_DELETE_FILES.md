# Renommage et suppression de fichiers
## Usage du renommage
1. Faire PATCH "/api/files/{id}/rename", l'id est tout simplement l'id du fichier sélectionné
2. Il faut aussi fournir dans un JSON le nouveau nom du fichier :
```json

{
  "newName": "RenamedFile"
}
```
3. Le backend s'occupe de remettre l'extension du fichier à partir de l'ancien nom donc pas besoin de gérer ça
4. Je te retourne les informations du fichier comme pour un GET avec le nom modifié bien sûr avec un code 200 OK

## Usage de la suppression
1. Faire DELETE "/api/files/{id}/delete", l'id est tout simplement l'id du fichier sélectionné
2. Rien faire d'autre, le backend s'occupe de la suppression du fichier et des ses enfants
3. Je te retourne un code 202 Accepted quand le fichier a été supprimé avec succès