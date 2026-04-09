# Guide de migration
Toutes les fichiers de migrations sont dans le dossier **"/migrations"**. <br/>
Un fichier de migration s'appelle un **"changelog"**. <br/>
Un changelog contient des **"changeset"** qui représente les changements effectués dans la bd. <br/>
Un changelog peut contenir plusieurs changeset.
Toujours créer un changelog en premier et le modèle après.

## Créer un changelog et un modèle
Malheureusement, la procédure est très manuelle mais, simple (Si les changelogs sont en sql). <br/>
### Changelog
1. **Créer un fichier .sql** dans le dossier **"src/main/resources/db/changelog/migrations"** <br/>
La date dans le format spécifié est obligatoire cependant, tant que la description est claire.
Le format de la description n'est qu'une suggestion.
- Syntaxe
``
YYYYMMDDHHmm_ACTION_NAME_TABLE.sql
`` <br/>
- Example <br/>
``
202604091621_create_user_table.sql
``
``
202604092014_add_email_column_on_user_table.sql
``
2. **La syntaxe du fichier sql** <br/>
```sql
--liquibase formatted sql

--changeset auteur:YYYYMMDDHHMM_file_name
/* du code sql */

--rollback /* code sql qui inverse le changeset */
```

Pour un example voir *20260408_create_users_table.sql*

3. **Redémarrez Spring Boot**

### Model
1. Créer une classe java dans **"src/main/java/xyz/lavoute/web/models"** <br/>

Checklist pour que le modèle soit accepté
- le modèle a un attribut id int avec une annotation <br/>
@Id et @GeneratedValue(strategy = GenerationType.IDENTITY)
- Annotation @Entity
- Annotation @NoArgsConstructor (ou un constructeur vide)
- Annotation @Table(name="example") <br/>
(non obligatoire si le nom de la table est la même que le modèle)
