# Migration guide
Les migrations ce retrouve dans **"resources/db/migration/"**. <br/>
Il n'y a **pas de rollback possible** dans la version community de Flyway. <br/> 
Si vous faite une erreur ou quoi, corrigez avec une autre migration. <br/>
# Creer une migration
Un fichier de migration est tout simplement un script sql. <br/>
Il n'y a pas de commentaire speciaux ou de headers special comme dans LiquiMerde. <br/>
## Types de migrations
Il y a 3 types de migration dont une non-disponible dans la community version. (C'est le rollback ou undo) <br/>
1. Versionned migration <br/>
   Une migration normale qui run dans une ordre numerique uniquement. <br/>
   Contient un identifiant unique dans son nom <br/>
   Ne peut pas etre changer par apres. <br/>
   <br/>
2. Repetitive <br/>
   Ne contient pas de identifiant numerique unique. <br/>
   Ce fichier peut etre modifier et reapplique la migration
   chaque fois quelle est modifie.

## Nomenclature du fichier
**Versionned Migration**
Dans la version community le nommage des migrations est stricte. <br/>
Si vous ne respectez pas la nomenclature ca explose. <br/>
La date c'est **notre propre convention** mais ont peut mettre n'importe quel numero tant que c'est dans l'ordre. <br/>
Comme un numero sequentiel ou autre. <br/>

### Versionning
**Le double ``_`` apres la date est importante, vous en mettez pas et ca explose**
``
V[YYYY.MM.DD.hh.mm]__[decription_nom_table].sql
``

### Repetitive
``
R[YYYY.MM.DD.hh.mm]__[decription_nom_table].sql
``

### Examples
Creer une table <br/>
``
V2026.04.13.10.48__create_users_table.sql
``

Ajouter une foreign key apres avoir creer une table <br/>
``
V2026.04.13.10.54__fk_parent_child.sql
``