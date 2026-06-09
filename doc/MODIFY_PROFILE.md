# ROUTE /api/user/edit

- Reçoit un UpdateProfileRequestDTO qui contient :

```json
{
  "firstName": "",
  "lastName": "",
  "oldPassword": "", //champ dans lequel l'utilisateur va entrer son ancien mot de passe (permet de valider avant de le modifier)
  "password": "" // Le nouveau mot de passe du compte après la sauvegarde
}
```
- Envoyer seulement les informations à changer (où les infos de base, ça va revenir au même)
- Je retourne un code OK avec un UserResponseDTO qui contient les infos à afficher si nécessaire (username, first name, last name, profile pic)