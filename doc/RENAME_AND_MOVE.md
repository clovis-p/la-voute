# Renommage et déplacement des fichiers
## Renommage
1. Faire PATCH "/api/files/{id]", l'id est l'id du fichier à renommer
2. Il faut aussi fournir en JSON le nouveau nom et l'id du parent actuel :
```json
{
  "newName": "LeNouveauNom",
  "newParentId": 2 //L'id du parent
}
```
*** IMPORTANT DE ME DONNER L'ID DU PARENT SINON ÇA VA DÉPLACER LE FICHIER merci

## Déplacement
1. Faire PATCH "/api/files/{id]", l'id est l'id du fichier à renommer
2. Comme pour le renommage, il faut aussi fournir en JSON LE NOM DU FICHIER ACTUEL et le ID du parent dans lequel on le déplace
```json
{
  "newName": "LeNomDuFichier",
  "newParentId": 5 //Mettre null si jamais c'est à la racine
}
```

- Je retourne un code BAD REQUEST (400) avec un message approprié quand que ça marche pas, bien penser de ne PAS METTRE LE NEWNAME EN NULL sinon t'as un 400 anyway
- je te retourne un FILEGETDTO en cas de réussite
