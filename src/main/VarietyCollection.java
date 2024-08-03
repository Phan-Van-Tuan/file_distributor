package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class VarietyCollection {
	private File sourceFolder;
	private File destFolder;
	private JTextField folderNameTextField;

	private JComboBox<String> fileExtComboBox;
	private JComboBox<String> folderNameComboBox;
	private HashMap<String, Integer> maxNumbers;

	private JPanel mainPanel;
	private JPanel folderPanel;

	private DataManager dataManager = new DataManager();

	public static void main(String[] args) {
		new VarietyCollection().createAndShowGUI();
	}

	private void createAndShowGUI() {
		final JFrame frame = new JFrame("Vendor Collection");
		frame.setSize(600, 560);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);

		// Main Panel
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		frame.add(mainPanel, BorderLayout.CENTER);

		// Footer Panel
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		footerPanel.setBackground(new Color(215, 215, 215));
		JLabel footerLabel = new JLabel(
				"<html>Variety Collection Application by JPatrick © 2024 <br> Wishing you creativity and success in every project.<br> Tuong Paul.</html>");
		footerPanel.add(footerLabel);
		frame.add(footerPanel, BorderLayout.SOUTH);

		// Source Folder Input
		JLabel sourceLabel = new JLabel("Source Folder:");
		sourceLabel.setBounds(50, 20, 100, 30);
		mainPanel.add(sourceLabel);

		final JTextField sourceField = new JTextField();
		sourceField.setBounds(160, 20, 300, 30);
		sourceField.setEditable(false);
		mainPanel.add(sourceField);

		JButton selectFolderButton = new JButton("Browse");
		selectFolderButton.setBounds(470, 20, 80, 30);
		mainPanel.add(selectFolderButton);

		// Destination Folder Input
		JLabel destLabel = new JLabel("Destination Folder:");
		destLabel.setBounds(50, 60, 120, 30);
		mainPanel.add(destLabel);

		final JTextField destField = new JTextField();
		destField.setBounds(160, 60, 300, 30);
		destField.setEditable(false);
		mainPanel.add(destField);

		JButton selectDestFolderButton = new JButton("Browse");
		selectDestFolderButton.setBounds(470, 60, 80, 30);
		mainPanel.add(selectDestFolderButton);

		// Folder Panels
		folderPanel = new JPanel();
		folderPanel.setBounds(50, 100, 500, 200);
		folderPanel.setLayout(new BoxLayout(folderPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(folderPanel);
		scrollPane.setBounds(50, 100, 500, 200);
		mainPanel.add(scrollPane);

		JLabel folderNumberLabel = new JLabel("Name of Folders:");
		folderNumberLabel.setBounds(50, 320, 200, 30);
		mainPanel.add(folderNumberLabel);

		String[] folderNames = { "Default" };
		folderNameComboBox = new JComboBox<>(folderNames);
		folderNameComboBox.setBounds(180, 320, 100, 30);
		mainPanel.add(folderNameComboBox);

		folderNameTextField = new JTextField("Folder");
		folderNameTextField.setBounds(300, 320, 100, 30);
		mainPanel.add(folderNameTextField);

		// File Extension Field
		JLabel fileExtLabel = new JLabel("File Extension:");
		fileExtLabel.setBounds(50, 360, 100, 30);
		mainPanel.add(fileExtLabel);

		String[] fileExtensions = { ".mp4", "All", ".txt", ".jpg", ".png", ".pdf", ".docx" };
		fileExtComboBox = new JComboBox<>(fileExtensions);
		fileExtComboBox.setBounds(180, 360, 100, 30);
		mainPanel.add(fileExtComboBox);

		// Copy Button
		JButton copyButton = new JButton("Copy Files");
		copyButton.setBounds(240, 420, 120, 30);
		mainPanel.add(copyButton);

		selectFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser folderChooser = new JFileChooser();
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = folderChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					sourceFolder = folderChooser.getSelectedFile();
					sourceField.setText(sourceFolder.getAbsolutePath());
					updateFolderPanel();
				}
			}
		});

		selectDestFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser folderChooser = new JFileChooser();
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = folderChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					destFolder = folderChooser.getSelectedFile();
					destField.setText(destFolder.getAbsolutePath());
					getListFolderName();
				}
			}
		});

		fileExtComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFolderPanel();
			}
		});

		folderNameComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String folderName = (String) folderNameComboBox.getSelectedItem();
				folderNameTextField.setText(folderName);
				;
			}
		});

		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sourceFolder == null || destFolder == null) {
					JOptionPane.showMessageDialog(frame, "Please select both source and destination folders.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					copyFiles();
					updateFolderPanel();
					getListFolderName();
				} catch (Exception error) {
					System.out.println(error);
					JOptionPane.showMessageDialog(null, error.getMessage());
					updateFolderPanel();
					getListFolderName();
				}
			}
		});
		frame.setVisible(true);
	}

	private void getListFolderName() {
		File[] subFolders = destFolder.listFiles(File::isDirectory);
		String[] listName = new String[subFolders.length];
		for (int i = 0; i < subFolders.length; i++) {
			listName[i] = subFolders[i].getName();
		}
		// Sử dụng HashMap để lưu trữ ký tự đầu tiên và số lớn nhất
		maxNumbers = new HashMap<>();

		for (String s : listName) {
			if (s.length() >= 3) {
				String suffix = s.substring(s.length() - 3); // Lấy 3 ký tự cuối cùng
				try {
					int number = Integer.parseInt(suffix); // Cố gắng chuyển đổi 3 ký tự cuối thành số
					String prefix = s.substring(0, s.length() - 3); // Phần còn lại của chuỗi là prefix

					// Kiểm tra và cập nhật số lớn nhất cho mỗi prefix
					if (!maxNumbers.containsKey(prefix) || maxNumbers.get(prefix) < number) {
						maxNumbers.put(prefix, number);
					}
				} catch (NumberFormatException e) {
					throw e;
				}
			}
		}

		// Hiển thị kết quả
		folderNameComboBox.removeAllItems();
		for (Map.Entry<String, Integer> entry : maxNumbers.entrySet()) {
			folderNameComboBox.addItem(entry.getKey());
			System.out.println("Prefix: " + entry.getKey() + ", Max Number: " + entry.getValue());
		}
	}

	private void updateFileCountLabel(JTextField fileCountLabel, File selectFolder) {
		if (selectFolder != null) {
			String fileExt = (String) fileExtComboBox.getSelectedItem();
			int count = 0;
			@SuppressWarnings("unused")
			int countAvailable = 0;
			for (File file : selectFolder.listFiles()) {
				if (file.isFile() && (fileExt.equals("All") || file.getName().endsWith(fileExt))) {
					count++;
					if (!dataManager.isFileCopied(file.getName())) {
						countAvailable++;
					}
				}
			}
			fileCountLabel.setText(countAvailable + "");
		} else {
			fileCountLabel.setText("");
		}
	}

	private void updateFolderPanel() {
		folderPanel.removeAll();

		File[] subFolders = sourceFolder.listFiles(File::isDirectory);

		if (subFolders != null) {
			for (File folder : subFolders) {
				JPanel folderItemPanel = new JPanel();
				folderItemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				folderItemPanel.setPreferredSize(new Dimension(450, 40));

				JTextField fileCountField = new JTextField(5);
				fileCountField.setText("0");
				fileCountField.setToolTipText("Number of files to copy from this folder");
				folderItemPanel.add(fileCountField);

				JTextField countLabel = new JTextField(5);
				countLabel.setEditable(false);
				folderItemPanel.add(countLabel);
				updateFileCountLabel(countLabel, folder);

				JLabel folderLabel = new JLabel(folder.getName());
				folderItemPanel.add(folderLabel);

				folderPanel.add(folderItemPanel);
			}
		}

		folderPanel.revalidate();
		folderPanel.repaint();
	}

	private void copyFiles() throws Exception {

		try {
			// get name and index of folder
			String folderNamePrefix = folderNameTextField.getText();
			int folderIndex = 0;
			if (maxNumbers.containsKey(folderNamePrefix)) {
				folderIndex = maxNumbers.get(folderNamePrefix);
			}

			// get number of loop
			String fileExt = (String) fileExtComboBox.getSelectedItem();
			File[] subFolders = sourceFolder.listFiles(File::isDirectory);

			if (subFolders == null || subFolders.length == 0) {
				throw new Exception("Source folder does not contain any subfolders!");
			}
			
			int[] target = new int[subFolders.length];

			int loopNumber = 9999;
			for (int i = 0; i < subFolders.length; i++) {
				JPanel folderItemPanel = (JPanel) folderPanel.getComponent(i);
				JTextField TargeNumField = (JTextField) folderItemPanel.getComponent(0);
				int fileTargetNum = Integer.parseInt(TargeNumField.getText());

				JTextField countAvailableFeild = (JTextField) folderItemPanel.getComponent(1);
				int fileAvailableNum = Integer.parseInt(countAvailableFeild.getText());

				target[i] = fileTargetNum;
				System.out.println("fileCountToCopy: " + fileAvailableNum + " // " + fileTargetNum);
				if (fileTargetNum > 0) {
					if (loopNumber > fileAvailableNum / fileTargetNum) {
						loopNumber = fileAvailableNum / fileTargetNum;
					}
				}
			}
			System.out.println("loop: " + loopNumber);
			if (loopNumber == 9999) {
				throw new Exception("Please enter number file you want to copy!");
			}

			for (int i = 0; i < loopNumber; i++) {
				String folderName = folderNamePrefix + String.format("%03d", folderIndex + 1);
				File newFolder = new File(destFolder, folderName);
				newFolder.mkdirs();
				folderIndex++;
				for (int j = 0; j < subFolders.length; j++) {
					List<File> filesInSubFolder = new ArrayList<>();
					for (File file : subFolders[j].listFiles()) {
						if (file.isFile() && (fileExt.equals("All") || file.getName().endsWith(fileExt))) {
							if (!dataManager.isFileCopied(file.getName())) {
								filesInSubFolder.add(file);
							}
						}
					}
					if (target[j] == 0) {
						continue;
					}
					Collections.shuffle(filesInSubFolder);
					File newSubFolder = new File(newFolder, subFolders[j].getName());
					newSubFolder.mkdirs();
					for (int o = 0; o < target[j]; o++) {
						File srcFile = filesInSubFolder.get(o);
						File destFile = new File(newSubFolder, srcFile.getName());
						Files.copy(srcFile.toPath(), destFile.toPath());
						dataManager.markFileAsCopied(srcFile.getName());
					}
				}
			}
			JOptionPane.showMessageDialog(null, "successfully!");

		} catch (Exception e) {
			throw e;
		}
	}
}