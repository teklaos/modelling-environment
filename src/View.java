import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class View extends JFrame {
    private final JList<String> modelsList;
    private final JList<String> dataList;
    private final DefaultTableModel tableModel;
    private final JPanel scriptModifiers;
    private final JButton runButton;
    private final JPanel outputData;
    private final JButton runFromFile;
    private final JButton createAndRun;

    public View() {
        JPanel windowPanel = new JPanel();
        windowPanel.setLayout(new BorderLayout());

        JPanel selection = new JPanel();
        selection.setLayout(new BorderLayout());
        windowPanel.add(selection, BorderLayout.WEST);

        JLabel jLabel = new JLabel("Select model and data");
        selection.add(jLabel, BorderLayout.NORTH);

        JPanel modelRunner = new JPanel();
        modelRunner.setLayout(new BorderLayout());
        selection.add(modelRunner, BorderLayout.CENTER);

        JPanel lists = new JPanel();
        modelsList = new JList<>();
        modelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        dataList = new JList<>();
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lists.add(new JScrollPane(modelsList));
        lists.add(new JScrollPane(dataList));

        lists.setLayout(new GridLayout(1, 2));
        modelRunner.add(lists, BorderLayout.CENTER);

        runButton = new JButton("Run model");
        modelRunner.add(runButton, BorderLayout.SOUTH);

        outputData = new JPanel();
        outputData.setLayout(new BorderLayout());
        windowPanel.add(outputData, BorderLayout.CENTER);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return getValueAt(0, columnIndex).getClass();
            }
        };
        JTable jTable = new JTable(tableModel);
        outputData.add(new JScrollPane(jTable), BorderLayout.CENTER);

        scriptModifiers = new JPanel();
        scriptModifiers.setLayout(new GridLayout(1, 2));

        runFromFile = new JButton("Run script from file");
        createAndRun = new JButton("Create and run script");

        scriptModifiers.add(runFromFile);
        scriptModifiers.add(createAndRun);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Modeling Framework Sample");
        setContentPane(windowPanel);
        setSize(650, 320);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JList<String> getModelsList() {
        return modelsList;
    }

    public JList<String> getDataList() {
        return dataList;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JPanel getScriptModifiers() {
        return scriptModifiers;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public JPanel getOutputData() {
        return outputData;
    }

    public JButton getRunFromFile() {
        return runFromFile;
    }

    public JButton getCreateAndRun() {
        return createAndRun;
    }
}
