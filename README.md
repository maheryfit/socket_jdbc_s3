Presenter par RAKOTOARISON Fitahiana Mahery, ETU 001821, Promotion 15 - A, W-73
Le theme du projet SOCKET est Le J.D.B.C

N.B: Les commandes telles que SELECT, FROM,... doivent etre en majuscules et que les noms de tables sont sensibles a la casse

BONUS : Traitement de plusieurs clients(Multithreading)

Commande existant :

__ select : SELECT * FROM [nom table]

__ select avec colonne : SELECT [colonnes] FROM [nom table]

__ select avec condition : SELECT * FROM [nom table] WHERE [colonne] = [valeur] [AND - OR [colonne] = [valeur]]

__ select avec colonne et condition : SELECT [colonnes] FROM [nom table] WHERE [colonne] = [valeur] [AND - OR [colonne] = [valeur]]

__ insertion : INSERT INTO [nom table] VALUES [valeur], [valeur], [valeur], ...

__ suppression : DELETE FROM [nom table]

__ suppression avec condition : DELETE FROM [nom table]  WHERE [colonne] = [valeur]

__ creation de table : CREATE TABLE [nom table] AND COLUMNS ARE [nom colonne], [nom colonne], ...

__ union (valable avec ou sans condition) : SELECT * FROM [nom table] UNION SELECT * FROM [meme nom table]

__ intersection (valable avec ou sans condition) : SELECT * FROM [nom table] INTERSECT SELECT * FROM [meme nom table]

__ difference (valable avec ou sans condition) : SELECT * FROM [nom table] NOT IN SELECT * FROM [meme nom table]

__ produit scalaire (valable avec ou sans condition) : SELECT * FROM [nom table] PRODUCT WITH SELECT * FROM [different nom table]

__ jointure (inner join) (valable avec ou sans condition) :  SELECT * FROM [nom table] JOIN SELECT * FROM [different nom table] ON [colonne qui est present dans les 2 tables]

__ division (inner join) (valable avec ou sans condition) :  SELECT * FROM [nom table] DIVIDE SELECT * FROM [different nom table] ON [colonne qui est present dans les 2 tables]

__ lister les tables presents : SHOW TABLE