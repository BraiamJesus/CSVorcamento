package com.mycompany.csvorc;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class OrcamentoApp extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public OrcamentoApp() {
        setTitle("Sistema de Orçamentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"Descrição", "Núm. Autorização", "Forma de aquisição", "Código de Referência", "DataAprovacaoProva",
            "OP", "Previsão de Expedição", "Orcamento PEM", "Origem Orçamento", "Status", "Cliente PEM",
            "CNPJ Cliente", "Telefone Cliente", "SKU", "Título", "Formato", "Papel Capa", "Cor Capa",
            "Gramatura Capa", "Orelhas", "Marcador", "Laminação", "Papel Miolo", "Cor Miolo", "Gramatura Miolo",
            "Número de Páginas", "Acabamento", "Shirink", "Observações Orçamento", "Observações Entrega", "Tiragem",
            "Endereço", "Número", "Bairro", "Complemento", "Cidade", "UF", "CEP", "Endereços Entrega",
            "Valor Unitário Sem Desconto", "Desconto (%)", "Valor Unitário Com Desconto", "Valor Total Com Desconto"};

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5)); // Ajustado para acomodar os 5 botões
        JButton addButton = new JButton("Adicionar Orçamento");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarOrcamento();
            }
        });
        JButton duplicateButton = new JButton("Duplicar Orçamento");
        duplicateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                duplicarOrcamento();
            }
        });
        JButton saveButton = new JButton("Salvar CSV");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarCSV();
            }
        });
        JButton deleteButton = new JButton("Excluir Orçamento");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirOrcamento();
            }
        });
        JButton pasteButton = new JButton("Colar");
        pasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colarDados();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(duplicateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(pasteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(150);
        }
    }

    private void adicionarOrcamento() {
        Vector<String> emptyRow = new Vector<>(Collections.nCopies(model.getColumnCount(), ""));
        model.addRow(emptyRow);
    }

    private void duplicarOrcamento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Vector<String> rowData = new Vector<>();
            for (int i = 0; i < model.getColumnCount(); i++) {
                rowData.add(model.getValueAt(selectedRow, i).toString());
            }
            model.addRow(rowData);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um orçamento para duplicar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void salvarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile() + ".csv");

                StringBuilder headerBuilder = new StringBuilder();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    headerBuilder.append("\"").append(model.getColumnName(i)).append("\"");
                    if (i < model.getColumnCount() - 1) {
                        headerBuilder.append(",");
                    }
                }
                writer.append(headerBuilder.toString()).append("\n");

                for (int i = 0; i < model.getRowCount(); i++) {
                    StringBuilder rowBuilder = new StringBuilder();
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        String value = model.getValueAt(i, j).toString();
                        rowBuilder.append("\"").append(value).append("\"");
                        if (j < model.getColumnCount() - 1) {
                            rowBuilder.append(",");
                        }
                    }
                    writer.append(rowBuilder.toString()).append("\n");
                }
                writer.close();
                JOptionPane.showMessageDialog(this, "CSV salvo com sucesso!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo CSV: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirOrcamento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um orçamento para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void colarDados() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

        if (hasTransferableText) {
            try {
                String[] lines = ((String) contents.getTransferData(DataFlavor.stringFlavor)).split("\n");
                for (String line : lines) {
                    String[] values = line.split("\t");
                    model.addRow(values);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao colar os dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OrcamentoApp().setVisible(true);
            }
        });
    }
}
