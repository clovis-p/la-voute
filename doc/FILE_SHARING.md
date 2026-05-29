# File Sharing

## Routes
- "/api/files/share/{fileId}/create" <br/>
  Prend une liste de username en json qui est optionelle. <br/>
  Retourne: 
  - Success -> 201 CREATED
  - Failure (Fichier non trouver) ->  400 BAD REQUEST
  - Failure (Username non trouver) -> 400 BAD REQUEST
- "/api/files/{fileId}" <br/>
  Retourne:
  - Success: 200 OK (ResponseEntity<FileGetDTO>)
  - Failure (Fichier non trouver) ->  400 BAD REQUEST
  - Failure (Username non trouver) -> 400 BAD REQUEST
  - Failure (Aucune condition vrai) -> 404 NOT FOUND
## Curl et Flow
C'est comment utiliser les endpoints avec curl donc a adapter lors du frontend
1. Get Csrf <br/>
`` curl -i -b cookies.txt -c cookies.txt http://localhost:8080/api/csrf``
2. Extract Token <br/>
``CSRF_TOKEN=$(awk '$6 == "XSRF-TOKEN" {print $7}' cookies.txt)``
3. Montrer le csrf token courant <br/>
``echo $CSRF_TOKEN``
4. Login <br/>
```
curl -i -b cookies.txt -c cookies.txt -X POST http://localhost:8080/login \
    -H "X-XSRF-TOKEN: $CSRF_TOKEN" \
    -d "username={username}" \
    -d "password={password}"
```
5. Prendre le nouveau le csrf associé avec la session et le user logged in <br/>
``Répété les étapes 1 à 3``
6. Call les endpoints
 - Rendre un fichier publique
   ```
   curl -b cookies.txt -X POST http://localhost:8080/api/files/share/{fileId}/create \
     -H "X-XSRF-TOKEN: $CSRF_TOKEN" \
     -H "Content-Type: application/json"
   ```
 - Partager un fichier avec un autre user
   ```
   curl -b cookies.txt -X POST http://localhost:8080/share/{fileId}/create \
     -H "X-XSRF-TOKEN: $CSRF_TOKEN" \
     -H "Content-Type: application/json" \
     -d '["usagerN"]'
   ```
 - Chercher un fichier
   ```
   curl -b cookies.txt http://localhost:8080/api/files/{fileId}
   ```