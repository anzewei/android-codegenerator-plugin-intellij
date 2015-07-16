package com.morcinek.android.codegenerator.plugin.ui;

import com.google.common.collect.Maps;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class CodeDialogBuilder {

    private final DialogBuilder dialogBuilder;

    private final JPanel topPanel;

//    private final JTextArea codeArea;
    private JBTextField packageText;
    private JComboBox sourcePathComboBox;
    private PsiFile mFile;

    public CodeDialogBuilder(Project project, String title, String producedCode) {
        dialogBuilder = new DialogBuilder(project);
        dialogBuilder.setTitle(title);

        JPanel centerPanel = new JPanel(new BorderLayout());
//        codeArea = prepareCodeArea(producedCode);
        Language language = JavaLanguage.INSTANCE;
        mFile = PsiFileFactory.getInstance(project).createFileFromText("d.java", language, producedCode);
        FileEditor editor = FileEditorProviderManager.getInstance().getProviders(project, mFile.getVirtualFile())[0].createEditor(project, mFile.getVirtualFile());

//        JScrollPane scrollPane = new JBScrollPane(codeArea);
        centerPanel.add(editor.getComponent(), BorderLayout.CENTER);
        dialogBuilder.setCenterPanel(centerPanel);

        topPanel = new JPanel(new GridLayout(0, 2));
        centerPanel.add(topPanel, BorderLayout.PAGE_START);

        dialogBuilder.removeAllActions();
    }

    public void addAction(String title, final Runnable action) {
        addAction(title, action, false);
    }

    public void addAction(String title, final Runnable action, final boolean runWriteAction) {
        dialogBuilder.addAction(new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runWriteAction) {
                    ApplicationManager.getApplication().runWriteAction(action);
                } else {
                    action.run();
                }
            }
        });
    }

    public void addPackageSection(String defaultText) {
        topPanel.add(new JLabel(StringResources.PACKAGE_LABEL));
        packageText = new JBTextField(defaultText);
        topPanel.add(packageText);
    }

    public String getPackage() {
        return packageText.getText();
    }

    public void addSourcePathSection(java.util.List<String> string, String defaultValue) {
        topPanel.add(new JLabel(StringResources.SOURCE_PATH_LABEL));
        sourcePathComboBox = new ComboBox(new CollectionComboBoxModel(string));
//        sourcePathComboBox.setSelectedIndex(0);
        topPanel.add(sourcePathComboBox);
    }

    public String getSourcePath() {
        return (String) sourcePathComboBox.getSelectedItem();
    }

    public int showDialog() {
        return dialogBuilder.show();
    }

    public void closeDialog(){
        dialogBuilder.getDialogWrapper().close(DialogWrapper.OK_EXIT_CODE);
    }

    public String getModifiedCode() {
        return mFile.getText();
    }

    private JTextArea prepareCodeArea(String producedCode) {
        JTextArea codeArea = new JTextArea(producedCode);
        codeArea.setBorder(new LineBorder(JBColor.gray));
        return codeArea;
    }
}
