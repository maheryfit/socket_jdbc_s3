/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg.base;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import mg.table.Table;
/**
 *
 * @author ITU
 */
public class SQL {
    
    private final LinkedList<String> tableExistants;
    {
        tableExistants = new LinkedList<>();
    }
    
    public SQL() {
       
    }
    
    private File getFile() {
        return new File(System.getProperty("user.dir")+"/field/table.txt");
    }
    
    private void insertToFileTable(String request) throws Exception {
        File fichier = getFile();
        try (FileWriter fileWriter = new FileWriter(fichier, true)) {
            String name = getNomTable(request, "AND COLUMNS ARE", "CREATE TABLE") + "&&";
            fileWriter.write(name);
        }
    }
    
    private String getNomTable(String request, String middle, String first) {
        String[] req = request.split(middle);
        String table = req[0].replace(first," ").trim();
        return table;
    }
    
    private String getNomTable(String request, String middle, String first, int position) {
        String[] req = request.split(middle);
        String[] table = req[0].split(first);
        return table[position];
    }
    
    private String getNomTable(String request, String middle) {
        String[] req = request.split(middle);
        String table = req[1].trim();
        return table;
    }
    
    private String getFileTableTXT() throws Exception {
        File file = getFile();
        String mot;
        try (FileReader fileReader = new FileReader(file)) {
            mot = "";
            int i;
            while ((i = fileReader.read()) != -1) {
                mot += String.valueOf((char) i);
            }
        }
        return mot;
    }
    
    private LinkedList<String> getFileTable() throws Exception {
        String colStr = getFileTableTXT();
        String[] tables = colStr.split("&&");
        for (String table : tables) {
            this.tableExistants.add(table.trim());
        }
        return this.tableExistants;
    }
    
    private boolean isTableExist(String nom) throws Exception {
        return getFileTable().contains(nom);
    }
    
    public void doRequest(String request) throws Exception {
        if (request.equals("SHOW TABLE")) {
            displayResult(getFileTable());
        } else if(request.contains((CharSequence)"DELETE FROM")) {
            String name;
            if (!request.contains((CharSequence)"WHERE")) {
                name = getNomTable(request, "FROM").trim();
            }
            else {
                name = getNomTable(request, "WHERE", "FROM", 1).trim();
            }
            if (!isTableExist(name)) {
                System.out.println("ERROR: YOU TRIED TO DELETE IN A TABLE WHO DOESN'T EXIST");
            } else {
                Table table = new Table(request);
                System.out.println("Data deleted");
            }
        }
        else if (request.contains((CharSequence)"CREATE TABLE") && request.contains((CharSequence)"AND COLUMNS ARE")) {
            String name = getNomTable(request, "AND COLUMNS ARE", "CREATE TABLE");
            if (!isTableExist(name)) {
                insertToFileTable(request);
                Table table = new Table(request);
                System.out.println("TABLE CREATED");
            } else {
                System.out.println("ERROR: THE TABLE ALREADY EXISTED");
            }
        } else if(request.contains((CharSequence)"INSERT INTO") && request.contains((CharSequence)"VALUES")) {
            String name = getNomTable(request, "VALUES", "INSERT INTO");
            if (isTableExist(name)) {
                try {
                    Table table = new Table(request);
                    System.out.println("Data add");
                } catch (Exception e) {
                    System.out.println(e.getMessage() + ", "+ e.getCause().toString());
                }
            } else {
                System.out.println("ERROR: YOU TRIED TO INSERT DATA IN A TABLE WHO DOESN'T EXIST");
            }
        } else if(request.contains((CharSequence)"SELECT") && request.contains((CharSequence)"FROM") && !request.contains((CharSequence)"UNION") && !request.contains((CharSequence)"INTERSECT") && !request.contains((CharSequence)"NOT IN") && !request.contains((CharSequence)"DIVIDE BY") && !request.contains((CharSequence)"PRODUCT WITH") && !request.contains((CharSequence)"JOIN")) {
            String name;
            if (request.contains("WHERE")) {
                String[] tab = request.split("FROM");
                String[] tab2 = tab[1].split("WHERE");
                name = tab2[0].trim();
            } else {
                name = getNomTable(request, "FROM");
            }
            if (isTableExist(name)) {
                Table table = new Table(request);
                LinkedList<String> tableaux = table.getDatasFetch();
                if (tableaux == null) {
                    System.out.println("ERROR: INVALID COLUMN");
                } else {
                    if (tableaux.isEmpty()) 
                        System.out.println("NO DATA SELECTED");
                    else 
                        displayResult(tableaux); 
                }
            } else {
                System.out.println("ERROR: YOU TRIED TO SELECT DATA IN A TABLE WHO DOESN'T EXIST");
            }
        } else if(request.contains((CharSequence)"SELECT") && request.contains((CharSequence)"FROM") || request.contains((CharSequence)"UNION") || request.contains((CharSequence)"INTERSECT") || request.contains((CharSequence)"NOT IN") || request.contains((CharSequence)"DIVIDE BY") || request.contains((CharSequence)"PRODUCT WITH") || request.contains((CharSequence)"JOIN")) {
            String nameTab1 = "";
            String nameTab2 = "";
            if (request.contains("UNION") || request.contains("INTERSECT") || request.contains("NOT IN")) {
                if (request.contains("UNION")) 
                    onlyUnionDifferenceIntersect(request, nameTab1, nameTab2, "UNION");
                else if (request.contains("INTERSECT")) 
                    onlyUnionDifferenceIntersect(request, nameTab1, nameTab2, "INTERSECT");
                else 
                    onlyUnionDifferenceIntersect(request, nameTab1, nameTab2, "NOT IN");
            } else if(request.contains("PRODUCT WITH") || request.contains("JOIN") || request.contains("DIVIDE BY")) {
                if (request.contains("PRODUCT WITH")) 
                    onlyProjectionJoinDivide(request, nameTab1, nameTab2, "PRODUCT WITH");
                if (request.contains("ON")) {
                    if(request.contains("JOIN"))
                        onlyProjectionJoinDivide(request, nameTab1, nameTab2, "JOIN");
                    else 
                        onlyProjectionJoinDivide(request, nameTab1, nameTab2, "DIVIDE BY");
                }
                else 
                    System.out.println("ERROR: INVALID COMMAND");
            }
        }
        else {
            System.out.println("ERROR: PLEASE REVIRIFY YOUR ORDER");
        }
    }
    
    private void onlyUnionDifferenceIntersect(String request, String nameTab1, String nameTab2, String splitter) throws Exception {
        String[] reqs = request.split(splitter);
        String[] a = getLesNomsDeTable(request, splitter);
        nameTab1 = a[0];
        nameTab2 = a[1];
        if (nameTab1.equals(nameTab2) == false || !isTableExist(nameTab1) || !isTableExist(nameTab2)) {
            System.out.println("ERROR: PLEASE REVIRIFY THE TABLE");
        } else {
            switch (splitter) {
                case "UNION":
                    displayResult(union(reqs[0], reqs[1]), "ERROR: COLUMNS CHOOSE IN BOTH REQUEST MUST BE THE SAME IN UNION");
                    break;
                case "INTERSECT":
                    displayResult(intersect(reqs[0], reqs[1]), "ERROR: COLUMNS CHOOSE IN BOTH REQUEST MUST BE THE SAME IN INTERSECT");
                    break;
                default:
                    displayResult(difference(reqs[0], reqs[1]), "ERROR: COLUMNS CHOOSE IN BOTH REQUEST MUST BE THE SAME IN DIFFERENCE");
                    break;
            }
        }
    }
    
    private String[] getLesNomsDeTable(String request, String separateur) {
        String[] aRetourner = new String[2];
        String[] reqs = request.split(separateur);
        if (reqs[0].contains("WHERE")) {
            String[] tab = reqs[0].split("FROM");
            String[] tab2 = tab[1].split("WHERE");
            aRetourner[0] = tab2[0].trim();
        } 
        else 
            aRetourner[0] = getNomTable(reqs[0], "FROM");
          
        if (reqs[1].contains("WHERE")) {
            String[] tab = reqs[1].split("FROM");
            String[] tab2 = tab[1].split("WHERE");
            aRetourner[1] = tab2[0].trim();
        }   
        else 
            aRetourner[1] = getNomTable(reqs[1], "FROM");
        return aRetourner;
    }
    
    private void displayResult(LinkedList<String> tableau) {
        tableau.forEach((action)-> {
            System.out.println(action);
        });
        if (tableau.size() <= 1) {
            System.out.println(tableau.size() + " row selected");
        } else
            System.out.println(tableau.size() + " rows selected");
    }
    
    private void displayResult(LinkedList<String> tableau, String error) {
        if (tableau.isEmpty()) {
            System.out.println(error);
        } else {
            displayResult(tableau);
        }
    }
    
    private String[] getColumnsInRequest(String request) {
        LinkedList<String> liste = new LinkedList<>();
        String[] separe = request.split("FROM");
        String col = separe[0].replace("SELECT", " ").trim();
        String[] lesColonnes = col.split(",");
        liste.addAll(Arrays.asList(lesColonnes));
        String[] aRetourner = new String[liste.size()];
        int i = 0;
        for (String lesColonne : lesColonnes) {
            aRetourner[i] = lesColonne.trim();
            i++;
        }
        return aRetourner;
    }
    
    private LinkedList<TreeMap<String, String>> stringToObject(LinkedList<String> d) throws Exception {
        LinkedList<TreeMap<String, String>> aRetourner = new LinkedList<>();
        d.stream().map((liste) -> liste.replace("{", " ").replace("}", " ").trim()).map((str) -> {
            TreeMap<String, String> toInsert = new TreeMap<>();
            String[] donnees = str.split(",");
            for (String donnee : donnees) {
                String[] dns = donnee.split(":");
                if (dns.length != 1) {
                    toInsert.put(dns[0].trim(), dns[1].trim());
                }
            }
            return toInsert;
        }).filter((toInsert) -> (!toInsert.isEmpty())).forEachOrdered((toInsert) -> {
            aRetourner.add(toInsert);
        });
        return aRetourner;
    }
    
    private LinkedList<String> union(String req1, String req2) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        LinkedList<String> liste1 = new Table(req1).getDatasFetch();
        LinkedList<String> liste2 = new Table(req2).getDatasFetch();
        if (isColumnsCompatible(req1, req2)) {
            liste1.forEach((list)->{
                aRetourner.add(list);
            });
            liste2.stream().filter((string) -> (!aRetourner.contains(string))).forEachOrdered((string) -> {
                aRetourner.add(string);
            });
        }
        return aRetourner;
    }
    
    private LinkedList<String> intersect(String req1, String req2) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        LinkedList<String> liste1 = new Table(req1).getDatasFetch();
        LinkedList<String> liste2 = new Table(req2).getDatasFetch();
        liste1.stream().filter((string) -> (liste2.contains(string))).forEachOrdered((string) -> {
            aRetourner.add(string);
        });
        return aRetourner;
    }
    
    private LinkedList<String> difference(String req1, String req2) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        LinkedList<String> liste1 = new Table(req1).getDatasFetch();
        LinkedList<String> liste2 = new Table(req2).getDatasFetch();
        liste1.stream().map((string) -> {
            return string;
        }).filter((string) -> (!liste2.contains(string) && !aRetourner.contains(string))).forEachOrdered((string) -> {
            aRetourner.add(string);
        });
        return aRetourner;
    }
    
    private LinkedList<String> difference(LinkedList<String> liste1, LinkedList<String> liste2) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        liste1.stream().map((string) -> {
            return string;
        }).filter((string) -> (!liste2.contains(string) && !aRetourner.contains(string))).forEachOrdered((string) -> {
            aRetourner.add(string);
        });
        return aRetourner;
    }
    
    private void onlyProjectionJoinDivide(String request, String name1, String name2, String splitter) throws Exception {
        String[] reqs = null;
        String[] a = null;
        String[] r = null;
        if (splitter.equals("PRODUCT WITH")) {
            reqs = request.split(splitter);
            a = getLesNomsDeTable(request, splitter);
            name1 = a[0];
            name2 = a[1];
        } else {
            r = request.split("ON");
            reqs = r[0].trim().split(splitter);
            a = getLesNomsDeTable(r[0], splitter);
            name1 = a[0];
            name2 = a[1];
        }
        if (name1.equals(name2) == true || !isTableExist(name1) || !isTableExist(name2)) {
            System.out.println("ERROR: PLEASE REVIRIFY THE TABLE");
        } else {
            switch (splitter) {
                case "PRODUCT WITH":
                    displayResult(produitScalaire(reqs[0], reqs[1]), "NO DATA SELECTED"); /// SELECT * FROM table1 PRODUCT WITH SELECT * FROM table2
                    break;
                    
                case "JOIN":
                    displayResult(jointure(reqs[0], reqs[1], r[1].trim()), "NO DATA SELECTED"); /// SELECT * FROM table1 JOIN SELECT * FROM table2 ON column(mitovy)
                    break;
                    
                default:
                    displayResult( division(reqs[0], reqs[1], r[1].trim()), "NO DATA SELECTED"); /// SELECT * FROM table1 DIVIDE BY SELECT * FROM table2 ON column(mitovy)
                    break;
            }
        }
    }
    
    private LinkedList<String> produitScalaire(String req1, String req2) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        LinkedList<String> liste1 = new Table(req1).getDatasFetch();
        LinkedList<String> liste2 = new Table(req2).getDatasFetch();
        liste1.forEach((string) -> {
            liste2.forEach((string1) -> {
                aRetourner.add(string + string1);
            });
        });
        return aRetourner;
    } 
    
     private String format(TreeMap<String, String> dt) throws Exception {
        String aRetourner = "{";
        int i = 0;
        for (Map.Entry<String, String> entry : dt.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            aRetourner += key + ":" + value;
            if (i != dt.size() - 1) {
                aRetourner += ",";
            }
            i++;
        }
        aRetourner += "}";
        return aRetourner;
    }
    
    private LinkedList<String> jointure(String req1, String req2, String foreignKey) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        LinkedList<String> liste1 = new Table(req1).getDatasFetch();
        LinkedList<String> liste2 = new Table(req2).getDatasFetch();
        LinkedList<TreeMap<String, String>> liste1s = stringToObject(liste1);
        LinkedList<TreeMap<String, String>> liste2s = stringToObject(liste2);
        for (TreeMap<String, String> ls : liste1s) {
            for (TreeMap<String, String> ls1 : liste2s) {
                if (ls.get("\"" + foreignKey + "\"").equals(ls1.get("\"" + foreignKey + "\""))) {
                    aRetourner.add(format(ls) + format(ls1));
                }
            }
        }
        return aRetourner;
    }
    
    private LinkedList<String> division(String req1, String req2, String foreignKey) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        LinkedList<String> lis1s = new Table(req1).getDatasFetch();
        LinkedList<String> d = divide(req1, req2, foreignKey);
        for (String lisl : lis1s) {
            for (String string : d) {
                if (lisl.contains(string)) {
                    aRetourner.add(lisl + "{" + string + "}");
                }
            }
        }
        return aRetourner;
    }
    
    private LinkedList<String> divide(String req1, String req2, String foreignKey) throws Exception {
        LinkedList<String> produits = stringToObjects(produitScalaire(req1, req2), foreignKey);
        LinkedList<String> joints = stringToObjects(jointure(req1, req2, foreignKey), foreignKey);
        LinkedList<String> dif1 = difference(produits, joints);
        return difference(produits, dif1);
    }
    
    private boolean isColumnsCompatible(String req1, String req2) throws Exception {
        String[] cols1 = getColumnsInRequest(req1);
        String[] cols2 = getColumnsInRequest(req2);
        for (String string : cols1) {
            for (String string1 : cols2) {
                if (string.equals(string1) == false) {
                    return false;
                }
            }
        }
        return true;
    }

    private LinkedList<String> stringToObjects(LinkedList<String> produits, String foreignKey) {
        LinkedList<String> aRetourner = new LinkedList<>();
        for (String produit : produits) {
            String[] rs = produit.replace("}{", "&&").split("&&");
            String r2 = rs[1].replace("}", " ").trim();
            String[] r2s = r2.split(",");
            String izy = "";
            for (String r21 : r2s) {
                if (r21.contains(foreignKey)) {
                    izy = r21.trim();
                    break;
                }
            }
            aRetourner.add(izy);
        }
        return aRetourner;
    }
}
