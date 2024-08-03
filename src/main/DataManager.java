package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DataManager {
	private static final String FILES_DAT = "product_name_files.dat";
    private ArrayList<String> copiedFiles;

    public DataManager() {
        loadCopiedFiles();
    }

    private void loadCopiedFiles() {
        copiedFiles = new ArrayList<>();
        File file = new File(FILES_DAT);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    copiedFiles.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
        	JOptionPane.showMessageDialog(null, "Files is not already!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isFileCopied(String fileName) {
        return copiedFiles.contains(fileName);
    }

    public void markFileAsCopied(String fileName) {
        copiedFiles.add(fileName);
        saveCopiedFiles();
    }

    private void saveCopiedFiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILES_DAT))) {
            for (String fileName : copiedFiles) {
                writer.write(fileName);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
