# La Voûte

Monolithe Spring Boot + Vue.js.
Environnement Docker Compose.

## Mise en place de l'environnement de développement local

Démarrer la base de données :

```bash
docker compose -f docker-compose.dev.yml up -d
```

Lancer le backend :

```bash
./mvnw spring-boot:run
```

Lancer le frontend (dans un terminal séparé) :

```bash
cd frontend
npm install
npm run dev
```

L'application est disponible sur `http://localhost:5173`. Vite redirige toutes les requêtes vers Spring Boot sur le port 8080.

## Production

Copier le fichier `.env.example` et renseigner les mots de passe :

```bash
cp .env.example .env
```

Modifier les valeurs dans `.env` :

```
DB_ROOT_PASSWORD=foobar
DB_PASSWORD=foobar
```

Compiler et démarrer:

```bash
docker compose -f docker-compose.prod.yml up -d
```

L'application est maintenant disponible sur `http://localhost:8080`.

Re-compiler après modification du code:

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

Arrêter:

```bash
docker compose -f docker-compose.prod.yml down
```

Arrêter et supprimer le volume de la base de données:

```bash
docker compose -f docker-compose.prod.yml down -v
```
## Guide de migration
Disponible dans "resources/db/changelog/MIGRATION.md"
