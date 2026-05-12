# Usage de la suppression
1. Faire DELETE "/api/files/{id}/delete", l'id est tout simplement l'id du fichier sélectionné
2. Rien faire d'autre, le backend s'occupe de la suppression du fichier et des ses enfants
3. Je te retourne un code 202 Accepted quand le fichier a été supprimé avec succès