package com.morcinek.android.codegenerator.plugin.actions;

import com.intellij.openapi.project.Project;
import com.morcinek.android.codegenerator.codegeneration.providers.ResourceProvidersFactory;
import com.morcinek.android.codegenerator.codegeneration.providers.factories.ActivityResourceProvidersFactory;
import com.morcinek.android.codegenerator.plugin.ui.CodeDialogBuilder;
import com.morcinek.android.codegenerator.plugin.utils.SourceRootUtils;

import java.io.IOException;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class ActivityAction extends LayoutAction {

    @Override
    protected String getResourceName() {
        return "Activity";
    }

    @Override
    protected String getTemplateName() {
        return "Activity_template";
    }

    @Override
    protected ResourceProvidersFactory getResourceProvidersFactory() {
        return new ActivityResourceProvidersFactory();
    }

    @Override
    protected void createOrOverrideFileWithGeneratedCode(CodeDialogBuilder codeDialogBuilder, Project project, String folderPath, String fileName) throws IOException {
        packageHelper.registerActivity(codeDialogBuilder.getPackage() + "." + fileName.substring(0, fileName.length() - 5));
        super.createOrOverrideFileWithGeneratedCode(codeDialogBuilder, project, folderPath, fileName);
    }
}
