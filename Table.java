/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg.table;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author ITU
 */
public class Table {
    private final LinkedList<String> colonnes;
    private final LinkedList<TreeMap<String, String>> datas;
    private LinkedList<String> datasFetch;
    {
        this.colonnes = new LinkedList<>();
        this.datas = new LinkedList<>();
        this.datasFetch = new LinkedList<>();
    }

    public Table(String request) throws Exception {
        if (request.contains((CharSequence) "CREATE TABLE") && request.contains((CharSequence) "AND COLUMNS ARE")) {
            insertNewTab(request);
        } else if (request.contains((CharSequence) "INSERT INTO") && request.contains((CharSequence) "VALUES")) {
            insertData(request);
        } else if (request.contains((CharSequence) "SELECT") && request.contains((CharSequence) "FROM")) {
            select(request);
        } else if (request.contains((CharSequence) "DELETE FROM")) {
            delete(request);
        } else if (request.contains((CharSequence) "DESC")) {
            describeTable(request);
        }
    }

    private LinkedList<String> describeTable(String request) throws Exception {
        String nameTable = request.split("DESC")[1].trim();
        String string = getFileTableTXT(nameTable);
        String columnString = string.split("]:")[1].trim();
        String[] columns = columnString.split(",");
        int count = 0;
        for (String column : columns) {
            count++;
            this.colonnes.add("[" + count + "]=>" + column);
        }
        return this.colonnes;
    }

    public LinkedList<String> getColonnes() {
        return this.colonnes;
    }

    private void delete(String request) throws Exception {
        String name;
        if (!request.contains((CharSequence) "WHERE")) {
            name = getNomTable(request, "FROM").trim();
            initializeFile(name);
        } else {
            name = getNomTable(request, "WHERE", "FROM", 1).trim();
            String[] req = request.split("WHERE");
            String[] col = req[1].split("=");
            String ajouter = apresSuppression(col[0].trim(), col[1].trim(), name);
            try (FileWriter fichier = new FileWriter(getDataFile(name), false)) {
                fichier.write(ajouter);
                fichier.flush();
            }
        }
    }

    private String apresSuppression(String column, String value, String nameTable) throws Exception {
        String data = "[";
        LinkedList<String> ds = selectionner(nameTable);
        LinkedList<String> fetch = new LinkedList<>();
        if (!value.matches("[+-]?\\d*(\\.\\d+)?")) {
            value = "\"" + value + "\"";
        }
        String cond = "\"" + column + "\":" + value;
        for (String d : ds) {
            if (!d.contains(cond)) {
                fetch.add(d);
            }
        }
        int count = 0;
        for (String string : fetch) {
            data += string;
            if (fetch.size() - 1 != count) {
                data += ",";
            }
            count++;
        }
        data += "]";
        return data;
    }

    public LinkedList<String> getDatasFetch() {
        return datasFetch;
    }

    private File getDataFile(String name) {
        return new File(System.getProperty("user.dir") + "/donnee/" + name + ".json");
    }

    private File getFile(String name) {
        return new File(System.getProperty("user.dir") + "/field/" + name + ".txt");
    }

    private String getNomTable(String request, String middle, String first) {
        String[] req = request.split(middle);
        String table = req[0].replace(first, " ").trim();
        return table;
    }

    private String getNomTable(String request, String middle) {
        String[] req = request.split(middle);
        String table = req[1].trim();
        return table;
    }

    private String getNomTable(String request, String middle, String first, int position) {
        String[] req = request.split(middle);
        String[] table = req[0].split(first);
        return table[position];
    }

    private LinkedList<String> getColonnes(String request, String splitteur) {
        String[] req = request.split(splitteur);
        String[] cols = req[1].split(",");
        for (String col : cols) {
            this.colonnes.add(col.trim());
        }
        return this.colonnes;
    }

    private void initializeFile(String nom) throws Exception {
        File f = getDataFile(nom);
        if (!f.exists()) {
            f.createNewFile();
        }
        try (FileWriter fileWriter = new FileWriter(f)) {
            fileWriter.write("[]");
            fileWriter.flush();
        }
    }

    private void insertNewTab(String request) throws Exception {
        String nom = getNomTable(request, "AND COLUMNS ARE", "CREATE TABLE");
        LinkedList<String> cols = getColonnes(request, "AND COLUMNS ARE");
        initializeFile(nom);
        File fichier = getFile(nom);
        try (FileWriter fileWriter = new FileWriter(fichier)) {
            String toWrite = "[" + nom + "]:";
            int count = 0;
            for (String col : cols) {
                toWrite += col;
                if (count != cols.size() - 1) {
                    toWrite += ",";
                }
                count++;
            }
            fileWriter.write(toWrite);
        }
    }

    private String getFileTableTXT(String nom) throws Exception {
        File file = getFile(nom);
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

    private LinkedList<String> getColonnesInTxt(String request, String middle, String first) throws Exception {
        String name = getNomTable(request, middle, first);
        String txt = getFileTableTXT(name);
        String[] cols = txt.split(":");
        String[] eachCol = cols[1].split(",");
        for (String string : eachCol) {
            colonnes.add(string);
        }
        return colonnes;
    }

    private LinkedList<String> getData(String request) {
        String[] req = request.split("VALUES");
        String[] dts = req[1].split(",");
        LinkedList<String> aRetourner = new LinkedList<>();
        for (String dt : dts) {
            aRetourner.add(dt.trim());
        }
        return aRetourner;
    }

    private void insertData(String request) throws Exception {
        String name = getNomTable(request, "VALUES", "INSERT INTO");
        LinkedList<String> dts = getData(request);
        LinkedList<String> cls = getColonnesInTxt(request, "VALUES", "INSERT INTO");
        if (cls.isEmpty() || dts.isEmpty() || cls.size() != dts.size()) {
            throw new Exception("ERREUR: INVALID DATA",
                    new Throwable("THE DATA YOU HAVE ENTERED DOESN'T COMPLY WITH THE COLUMNS"));
        }
        String data = "[";
        File file = getDataFile(name);
        if (!file.createNewFile()) {
            data += this.fetchDataInFile(name) + ",";
        }
        String toWrite = "{";
        for (int i = 0; i < dts.size(); i++) {
            toWrite += "\"" + cls.get(i) + "\":";
            String tmp = "";
            if (dts.get(i).matches("[+-]?\\d*(\\.\\d+)?")) { /// Tester si string est un nombre ou non
                tmp = dts.get(i);
            } else {
                tmp = "\"" + dts.get(i) + "\"";
            }
            toWrite += tmp;
            if (i != dts.size() - 1) {
                toWrite += ",";
            }
        }
        toWrite += "}]";
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(data);
            writer.write(toWrite);
        }
    }

    private String fetchDataInFile(String name) throws Exception {
        File fichier;
        fichier = getDataFile(name);
        String data;
        try (Scanner scanner = new Scanner(fichier)) {
            data = "";
            while (scanner.hasNext()) {
                data = scanner.nextLine();
            }
        }
        String aRetourner = data.replace('[', ' ').replace(']', ' ').trim();
        return aRetourner;
    }

    private String[] retrieveData(String name) throws Exception {
        if (fetchDataInFile(name).equals("")) {
            throw new Exception("THERE IS NO DATA IN THIS TABLE");
        }
        String[] tabs = fetchDataInFile(name).split("},");
        for (int i = 0; i < tabs.length - 1; i++) {
            String concat = tabs[i].concat("}").trim();
            tabs[i] = concat;
        }
        return tabs;
    }

    private LinkedList<TreeMap<String, String>> stringToObject(String name) throws Exception {
        String[] d = retrieveData(name);
        for (String string : d) {
            TreeMap<String, String> map = new TreeMap<>();
            String str = string.replace("{", " ").trim();
            str = str.replace("}", " ").trim();
            String[] cols = str.split(",");
            for (String colWithout : cols) {
                String[] keyValue = colWithout.split(":");
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
            this.datas.add(map);
        }
        return this.datas;
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

    private LinkedList<String> selectionner(String name) throws Exception {
        LinkedList<TreeMap<String, String>> dts = stringToObject(name);
        LinkedList<String> aRetourner = new LinkedList<>();
        dts.stream().map((dt) -> {
            String inside = "{";
            int count = 0;
            for (Map.Entry<String, String> entry : dt.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                inside += key + ":" + value;
                if (count != dt.size() - 1) {
                    inside += ",";
                }
                count++;
            }
            return inside;
        }).map((inside) -> {
            inside += "}";
            return inside;
        }).forEachOrdered((inside) -> {
            if (!inside.trim().equals(" ".trim())) {
                aRetourner.add(inside);
            }
        });
        return aRetourner;
    }

    private void select(String request) throws Exception {
        if (request.contains((CharSequence) "*") && !request.contains((CharSequence) "WHERE")) {
            String name = getNomTable(request, "FROM");
            this.datasFetch = selectionner(name);
        } else if (!request.contains((CharSequence) "*") && !request.contains((CharSequence) "WHERE")) {
            String name = getNomTable(request, "FROM");
            String[] cols = getColumnsInRequest(request);
            this.datasFetch = selectionner(name, cols);
        } else if (request.contains((CharSequence) "*") && request.contains((CharSequence) "WHERE")) {
            String[] tab = request.split("FROM");
            String[] tab2 = tab[1].split("WHERE");
            String name = tab2[0].trim();
            String[] val = request.split("WHERE");
            if (!isAnotherConditions(request)) {
                String[] cond = val[1].split("=");
                this.datasFetch = selectionner(name, cond[0].trim(), cond[1].trim());
            } else {
                if (val[1].contains("OR")) {
                    String[] conds = val[1].split("OR");
                    String[] cond1 = conds[0].trim().split("=");
                    String[] cond2 = conds[1].trim().split("=");
                    if (!cond1[0].trim().equals(cond2[0].trim())) {
                        throw new Exception("ERROR: WHEN WE USE 'OR' THE COLUMNS MUST BE MENTIONNED TWICE");
                    } else {
                        LinkedList<String> listeStr1 = selectionner(name, cond1[0].trim(), cond1[1].trim());
                        LinkedList<String> listeStr2 = selectionner(name, cond2[0].trim(), cond2[1].trim());
                        this.datasFetch = union(listeStr1, listeStr2);
                    }
                } else if (val[1].contains("AND")) {
                    String[] conds = val[1].split("AND");
                    String[] cond1 = conds[0].trim().split("=");
                    String[] cond2 = conds[1].trim().split("=");
                    if (cond1[0].trim().equals(cond2[0].trim())) {
                        throw new Exception("ERROR: WHEN WE USE 'AND' THE COLUMNS CANT'T BE MENTIONNED TWICE");
                    } else {
                        LinkedList<String> listeStr1 = selectionner(name, cond1[0].trim(), cond1[1].trim());
                        LinkedList<String> listeStr2 = selectionner(listeStr1, cond2[0].trim(), cond2[1].trim());
                        this.datasFetch = listeStr2;
                    }
                } else {
                    this.datasFetch = new LinkedList<>();
                }
            }
        } else if (!request.contains((CharSequence) "*") && request.contains((CharSequence) "WHERE")) {
            String[] tab = request.split("FROM");
            String[] tab2 = tab[1].split("WHERE");
            String name = tab2[0].trim();
            String[] val = request.split("WHERE");
            if (!isAnotherConditions(request)) {
                String[] cond = val[1].split("=");
                String[] cols = getColumnsInRequest(request);
                LinkedList<String> str = selectionner(name, cond[0].trim(), cond[1].trim());
                this.datasFetch = selectionner(str, cols);
            } else {
                if (val[1].contains("OR")) {
                    String[] conds = val[1].split("OR");
                    String[] cond1 = conds[0].trim().split("=");
                    String[] cond2 = conds[1].trim().split("=");
                    if (!cond1[0].trim().equals(cond2[0].trim())) {
                        throw new Exception("ERROR: WHEN WE USE 'OR' THE COLUMNS MUST BE MENTIONNED TWICE");
                    } else {
                        LinkedList<String> listeStr1 = selectionner(name, cond1[0].trim(), cond1[1].trim());
                        LinkedList<String> listeStr2 = selectionner(name, cond2[0].trim(), cond2[1].trim());
                        this.datasFetch = union(listeStr1, listeStr2);
                    }
                } else if (val[1].contains("AND")) {
                    String[] conds = val[1].split("AND");
                    String[] cond1 = conds[0].trim().split("=");
                    String[] cond2 = conds[1].trim().split("=");
                    if (cond1[0].trim().equals(cond2[0].trim())) {
                        throw new Exception("ERROR: WHEN WE USE 'AND' THE COLUMNS CANT'T BE MENTIONNED TWICE");
                    } else {
                        LinkedList<String> listeStr1 = selectionner(name, cond1[0].trim(), cond1[1].trim());
                        LinkedList<String> listeStr2 = selectionner(listeStr1, cond2[0].trim(), cond2[1].trim());
                        this.datasFetch = listeStr2;
                    }
                } else
                    this.datasFetch = new LinkedList<>();
            }
        }
    }

    private static LinkedList<String> union(LinkedList<String> table1, LinkedList<String> table2) {
        LinkedList<String> newTab = new LinkedList<>();
        table1.forEach((tab) -> {
            newTab.add(tab);
        });
        table2.forEach((tab) -> {
            if (!newTab.contains(tab)) {
                newTab.add(tab);
            }
        });
        return newTab;
    }

    private LinkedList<String> selectionner(String name, String... columns) throws Exception {
        LinkedList<TreeMap<String, String>> dts = stringToObject(name);
        return trtColumns(dts, columns);
    }

    private LinkedList<String> trtColumns(LinkedList<TreeMap<String, String>> dts, String... columns) throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        dts.stream().map((dt) -> {
            String r = "{";
            int i = 0;
            for (String column : columns) {
                r += "\"" + column + "\":" + dt.get("\"" + column + "\"");
                if (i != columns.length - 1)
                    r += ",";
                i++;
            }
            return r;
        }).map((r) -> {
            r += "}";
            return r;
        }).forEachOrdered((r) -> {
            if (!r.trim().equals(" ".trim())) {
                aRetourner.add(r);
            }
        });
        if (containsNull(aRetourner)) {
            return null;
        }
        return aRetourner;
    }

    private LinkedList<String> trtConditions(LinkedList<TreeMap<String, String>> dts, String column, String valeur)
            throws Exception {
        LinkedList<String> aRetourner = new LinkedList<>();
        for (TreeMap<String, String> dt : dts) {
            String huhu = new String();
            if (!valeur.matches("[+-]?\\d*(\\.\\d+)?")) {
                valeur = "\"" + valeur + "\"";
            }
            String str = dt.get("\"" + column + "\"").replace("\"", " ").trim();
            if (str.compareTo(valeur.replace("\"", " ").trim()) == 0) {
                huhu = format(dt);
            }
            if (!huhu.trim().equals(" ".trim())) {
                aRetourner.add(huhu);
            }
        }
        if (containsNull(aRetourner)) {
            return null;
        }
        return aRetourner;
    }

    private LinkedList<String> selectionner(LinkedList<String> listes, String... columns) throws Exception {
        LinkedList<TreeMap<String, String>> dts = stringToObject(listes);
        return trtColumns(dts, columns);
    }

    private LinkedList<String> selectionner(String name, String column, String valeur) throws Exception {
        LinkedList<TreeMap<String, String>> dts = stringToObject(name);
        return trtConditions(dts, column, valeur);
    }

    private LinkedList<String> selectionner(LinkedList<String> listes, String column, String valeur) throws Exception {
        LinkedList<TreeMap<String, String>> dts = stringToObject(listes);
        return trtConditions(dts, column, valeur);
    }

    private boolean isAnotherConditions(String request) {
        String[] req = request.split("WHERE");
        return req[1].contains("AND") || req[1].contains("OR");
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

    private boolean containsNull(LinkedList<String> strings) {
        return strings.stream().anyMatch((string) -> (string.contains((CharSequence) "null")));
    }

    private String[] getColumnsInRequest(String request) {
        LinkedList<String> liste = new LinkedList<>();
        String[] separe = request.split("FROM");
        String col = separe[0].replace("SELECT", " ").trim();
        String[] aRetourner = null;
        String[] lesColonnes = col.split(",");
        liste.addAll(Arrays.asList(lesColonnes));
        aRetourner = new String[liste.size()];
        int i = 0;
        for (String lesColonne : liste) {
            aRetourner[i] = lesColonne.trim();
            i++;
        }
        return aRetourner;
    }
}
