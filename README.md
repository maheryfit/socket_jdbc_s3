# Presenter par RAKOTOARISON Fitahiana Mahery, ETU 001821, Promotion 15 - A, W-73

Le theme du projet SOCKET est Le S.G.B.D

Langage : JAVA

N.B: Les commandes telles que SELECT, FROM,... doivent etre en majuscules et que les noms de tables et les colonnes sont sensibles a la casse

BONUS : Traitement de plusieurs clients(Multithreading)

Commande existant :

__ creation de table : CREATE TABLE nom_table AND COLUMNS ARE nom_colonne, nom_colonne, ...

__ decrire une table : DESC nom_table

__ insertion : INSERT INTO nom_table VALUES valeur, valeur, valeur, ...

__ select : SELECT * FROM nom_table

__ select avec colonne : SELECT colonnes FROM nom table

__ select avec condition : SELECT * FROM nom_table WHERE colonne = valeur [AND - OR [colonne] = [valeur]]

__ select avec colonne et condition : SELECT colonnes FROM nom_table WHERE colonne = valeur [AND - OR [colonne] = [valeur]]

__ suppression avec condition : DELETE FROM nom_table  WHERE colonne = valeur

__ union (valable avec ou sans condition) : SELECT * FROM nom_table UNION SELECT * FROM meme_nom_table

__ intersection (valable avec ou sans condition) : SELECT * FROM nom_table INTERSECT SELECT * FROM meme_nom_table

__ difference (valable avec ou sans condition) : SELECT * FROM nom_table NOT IN SELECT * FROM meme_nom_table

__ produit scalaire (valable avec ou sans condition) : SELECT * FROM nom_table PRODUCT WITH SELECT * FROM different_nom_table

__ jointure (inner join) (valable avec ou sans condition) :  SELECT * FROM nom_table JOIN SELECT * FROM different_nom_table ON colonne_qui_est_present_dans_les_2_ tables

__ lister les tables presents : SHOW TABLES

__ supprimer la table : DROP nom_table

__ sous requete : SELECT ... FROM FETCH FROM SELECT ... FROM ... LIMIT ...

# Tester requete :

__ compile Server: java principale.ServerMain 

__ compile Client: java principale.ClientMain

__ creation de table : CREATE TABLE Voiture AND COLUMNS ARE idVoiture, marque, origine

__ creation de table : CREATE TABLE Chauffeur AND COLUMNS ARE idChauffeur, nomChauffeur, idVoiture

__ lister les tables presents : SHOW TABLES

__ decrire une table : DESC Chauffeur

__ insertion : INSERT INTO Voiture VALUES 1, Mercedes, USA

__ insertion : INSERT INTO Voiture VALUES 2, Ford, USA

__ insertion : INSERT INTO Voiture VALUES 3, Kia, Coree

__ insertion : INSERT INTO Voiture VALUES 4, Toyota, Japon

__ insertion : INSERT INTO Voiture VALUES 5, Bugatti, France

__ insertion : INSERT INTO Voiture VALUES 6, Lamborghini, Italie

__ insertion : INSERT INTO Voiture VALUES 7, Ferrari, Italie

__ insertion : INSERT INTO Chauffeur VALUES 1,Marquez, 1

__ insertion : INSERT INTO Chauffeur VALUES 2,Fabio, 1

__ insertion : INSERT INTO Chauffeur VALUES 3,Zarco , 2

__ insertion : INSERT INTO Chauffeur VALUES 4,Hamilton, 3

__ insertion : INSERT INTO Chauffeur VALUES 5,Verstappen, 4

__ insertion : INSERT INTO Chauffeur VALUES 6,Max, 4

__ selection : SELECT * FROM Voiture

__ suppression avec condition : DELETE FROM Voiture WHERE idVoiture = 7

__ selection : SELECT idChauffeur, nomChauffeur FROM Chauffeur

__ selection : SELECT * FROM Chauffeur WHERE idChauffeur = 3 OR idChauffeur =4

__ selection : SELECT idVoiture, marque FROM FETCH FROM SELECT * FROM Voiture WHERE origine = USA

__ selection : SELECT idVoiture, marque FROM FETCH FROM SELECT * FROM Voiture WHERE origine = USA LIMIT WHERE idVoiture = 1

__ selection difference : SELECT * FROM Chauffeur NOT IN SELECT * FROM Chauffeur WHERE idChauffeur = 1

__ selection intersection : SELECT * FROM Chauffeur INTERSECT SELECT * FROM Chauffeur WHERE idChauffeur = 1

__ selection produit scalaire : SELECT * FROM Chauffeur PRODUCT WITH SELECT * FROM Voiture

__ selection jointure : SELECT * FROM Chauffeur JOIN SELECT * FROM Voiture ON idVoiture

__ supprimer table (N'existe plus dans table.txt && dans repertoire donnee et field) : DROP Chauffeur 
