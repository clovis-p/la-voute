# ROUTE /register

- Reçoit une UserDTO qui contient : 
    - le username de l'utilisateur
    - le prénom de l'utilisateur
    - le nom de l'utilisateur
    - le mot de passe de l'utilisateur


- Il faut donc faire un POST /register avec un body JSON contenant ces champs :
    - {
    - "username": "string (obligatoire)",
    - "firstName": "string (obligatoire)",
    - "lastName": "string (obligatoire)",
    - "password": "string (obligatoire)"
    - }


- Pour la validation frontend :
    - le prénom, le nom et le username doit être entre 3 et 50 caractères
    - le mot de passe doit être entre 8 et 100 caractères, une lettre majuscule, une lettre minuscule, un chiffre et un symbole


- Réponses :
    - 201 Created: utilisateur créé
    - 400 Bad Request: données invalides
  


  