# Authentication
## Usage
1. Faire GET "/api/csrf" pour recevoir un csrf token <br/>
   (Le browser le store automatiquement donc littéralement juste faire un get request)
2. Faire un POST "/login" avec username et password dans un form data
   - https://developer.mozilla.org/en-US/docs/Web/API/FormData
   - le content type est "application/x-www-form-urlencoded" <br/>
     (le browser le fait automatique)
3. Et maintenant toutes les requêtes subséquentes seront authentifiées et identifié avec l'identité du user

## API 

- "/login" <br/>
  Autogenéré et geré par Spring Security. 
- "/api/csrf" <br/>
  Endpoint qui retourne un 200 ok. <br/>
  Spring Security est lazy et ne génère un csrf token que si il y en a de besoin pour sauver des ressources.
  Le code fait juste toucher au csrf pour que Spring soit forcé dans générer un.
  L'utilisateur est rappelé pendant 30 minutes sans aucune activité.
  L'utilisateur ce fait oublier si
  - Il ferme le browser
  - Sa session se fait invalider par le backend
  - 30 minutes sans requête
  - Si il navigue en private/incognito mode
