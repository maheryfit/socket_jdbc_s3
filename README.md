Presenter par RAKOTOARISON Fitahiana Mahery, ETU 001821, Promotion 15 - A, W-73
Le theme du projet SOCKET est Le S.G.B.D

N.B: Les commandes telles que SELECT, FROM,... doivent etre en majuscules et que les noms de tables sont sensibles a la casse

BONUS : Traitement de plusieurs clients(Multithreading)

Commande existant :

__ select : SELECT * FROM nom_table

__ select avec colonne : SELECT colonnes FROM nom table

__ select avec condition : SELECT * FROM nom_table WHERE colonne = valeur [AND - OR [colonne] = [valeur]]

__ select avec colonne et condition : SELECT colonnes FROM nom_table WHERE colonne = valeur [AND - OR [colonne] = [valeur]]

__ insertion : INSERT INTO nom_table VALUES valeur, valeur, valeur, ...

__ suppression : DELETE FROM nom_table

__ suppression avec condition : DELETE FROM nom_table  WHERE colonne = valeur

__ creation de table : CREATE TABLE nom_table AND COLUMNS ARE nom_colonne, nom_colonne, ...

__ union (valable avec ou sans condition) : SELECT * FROM nom_table UNION SELECT * FROM meme_nom_table

__ intersection (valable avec ou sans condition) : SELECT * FROM nom_table INTERSECT SELECT * FROM meme_nom_table

__ difference (valable avec ou sans condition) : SELECT * FROM nom_table NOT IN SELECT * FROM meme_nom_table

__ produit scalaire (valable avec ou sans condition) : SELECT * FROM nom_table PRODUCT WITH SELECT * FROM different_nom_table

__ jointure (inner join) (valable avec ou sans condition) :  SELECT * FROM nom_table JOIN SELECT * FROM different_nom_table ON colonne_qui_est_present_dans_les_2_ tables

__ division (valable avec ou sans condition) :  SELECT * FROM nom_table DIVIDE SELECT * FROM different_nom_table ON colonne_qui_est_present_dans_les_2_tables

__ lister les tables presents : SHOW TABLES

__ lister les noms de colonnes d'une table : DESC nom_table
