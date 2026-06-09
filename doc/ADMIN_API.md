## Usage of the admin api

# Avoir une liste des utilisateurs

- Call /api/admin/obtain pour avoir une liste de tous les users avec un DTO qui contient :
    - id
    - username
    - first name
    - last name
    - profile picture (ig que c'est bien pour modérer des pfp NSFW lol)


# Supprimer un utilisateur

- Call /api/admin/{id}/delete pour supprimer un utilisateur
- l'id c'est l'id de l'utilisateur à supprimer
- c'est tout, retourne un code 200 si ça marche

Merci d'avoir lu Clovis