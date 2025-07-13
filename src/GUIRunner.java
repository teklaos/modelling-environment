import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.*;

public class GUIRunner {
    private View view;
    private Controller controller;

    public GUIRunner() {
        createView();
    }

    private void createView() {
        view = new View();
        setModelsAndData();
        addButtonListeners();
    }

    private void setModelsAndData() {
        File models = new File("src/models");
        File[] modelsList = models.listFiles();
        String[] modelsNames = null;
        if (modelsList != null) {
            modelsNames = new String[modelsList.length];

            for (int i = 0; i < modelsList.length; i++) {
                if (!modelsList[i].getName().equals("Bind.java")) {
                    modelsNames[i] = modelsList[i].getName().replaceAll(".java", "");
                }
            }
        }

        File data = new File("data");
        File[] dataList = data.listFiles();
        String[] dataNames = null;
        if (dataList != null) {
            dataNames = new String[dataList.length];

            for (int i = 0; i < dataList.length; i++) {
                dataNames[i] = dataList[i].getName();
            }
        }

        view.getModelsList().setListData(modelsNames);
        view.getDataList().setListData(dataNames);
    }

    private void addButtonListeners() {
        view.getRunButton().addActionListener((e) -> {
            String selectedModel = view.getModelsList().getSelectedValue();
            String selectedData = view.getDataList().getSelectedValue();
            if (selectedModel != null && selectedData != null) {
                String dataDir = "data/";
                controller = new Controller(selectedModel);
                controller.readDataFrom(dataDir + selectedData).runModel();

                Object[][] tableData = getDataFromTsv();
                Object[] columnNames = getColumnNamesFromTsv();

                view.getTableModel().setDataVector(tableData, columnNames);
                view.getTableModel().fireTableDataChanged();

                view.getOutputData().add(view.getScriptModifiers(), BorderLayout.SOUTH);
                view.getOutputData().revalidate();
                view.getOutputData().repaint();
            }
        });

        view.getRunFromFile().addActionListener(event -> {
            String projectDirectory = System.getProperty("user.dir");
            File defaultDirectory = new File(projectDirectory).getParentFile();
            JFileChooser fileChooser = new JFileChooser(defaultDirectory);
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                controller.runScriptFromFile(selectedFile.toString());
            }

            Object[][] tableData = getDataFromTsv();
            Object[] columnNames = getColumnNamesFromTsv();

            view.getTableModel().setDataVector(tableData, columnNames);
            view.getTableModel().fireTableDataChanged();
        });

        view.getCreateAndRun().addActionListener(event -> {
            JFrame jFrame = new JFrame("Script");

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());

            JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea);
            jPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttons = new JPanel();
            buttons.setLayout(new FlowLayout());
            jPanel.add(buttons, BorderLayout.SOUTH);

            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            buttons.add(okButton);
            buttons.add(cancelButton);

            okButton.addActionListener(e -> {
                controller.runScript(textArea.getText());

                Object[][] tableData = getDataFromTsv();
                Object[] columnNames = getColumnNamesFromTsv();

                view.getTableModel().setDataVector(tableData, columnNames);
                view.getTableModel().fireTableDataChanged();

                jFrame.dispose();
            });

            cancelButton.addActionListener(e -> jFrame.dispose());

            jFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jFrame.setContentPane(jPanel);
            jFrame.setSize(250, 200);
            jFrame.setLocationRelativeTo(view.getCreateAndRun());
            jFrame.setVisible(true);
        });
    }

    private Object[][] getDataFromTsv() {
        String[] rows = controller.getResultsAsTsv().split("\n");
        int numOfRows = rows.length;
        int numOfColumns = rows[0].split("\t").length;

        Object[][] tableData = new Object[numOfRows][numOfColumns];

        for (int i = 0; i < numOfRows; i++) {
            String[] columns;
            for (int j = 0; j < numOfColumns; j++) {
                columns = rows[i].split("\t");
                try {
                    double formattedValue = formatValue(Double.parseDouble(columns[j]));
                    tableData[i][j] = formattedValue;
                } catch (NumberFormatException ex) {
                    tableData[i][j] = columns[j];
                }
            }
        }
        return tableData;
    }

    private Object[] getColumnNamesFromTsv() {
        List<String> columnNamesAsList = new ArrayList<>();
        columnNamesAsList.add("");
        columnNamesAsList.addAll(controller.getYearsList());
        columnNamesAsList.remove("LATA");
        return columnNamesAsList.toArray();
    }

    private double formatValue(double value) {
        String fString;
        if (value < 1) {
            fString = String.format("%.3f", value);
        } else if (value >= 1 && value < 50) {
            fString = String.format("%.2f", value);
        } else {
            fString = String.format("%.1f", value);
        }
        return Double.parseDouble(fString);
    }
}