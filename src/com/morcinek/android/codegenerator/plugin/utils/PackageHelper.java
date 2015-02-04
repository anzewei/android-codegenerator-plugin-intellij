package com.morcinek.android.codegenerator.plugin.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;

import java.util.ArrayList;

/**
 * Copyright 2014 Tomasz Morcinek. All rights reserved.
 */
public class PackageHelper {
    private Module module;
    private String packageName;
    public void setModule(Module module) {
        this.module = module;
        packageName = getPackageName(module);
    }

    public Module getModule()
    {
        return module;
    }
    public PackageHelper() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void registerActivity(final String className){
        register(module,className,packageName);
    }

    public static String getPackageName(Module module){
        PsiFile manifest = getManifastFile(module);
        final ArrayList<XmlTag> root = new ArrayList<XmlTag>();
        manifest.accept(new XmlRecursiveElementVisitor() {

            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);

                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;

                    if (tag.getName().equalsIgnoreCase("manifest")) {
                        root.add(tag);
                        return;
                    }
                }}
        });
        if (root.size() > 0 ){
            return root.get(0).getAttributeValue("package");
        }
        return "";
    }

    public static  PsiFile getManifastFile(Module module){
        PsiFile[] files = FilenameIndex.getFilesByName(module.getProject(), "AndroidManifest.xml", module.getModuleContentScope());
        if (files.length <= 0) {
            return null; //no matching files
        }

        VirtualFile buildDir =  ModuleRootManager.getInstance(module).getContentRoots()[0].findChild("build");
        for (PsiFile manifest : files){
            if (!(buildDir != null && manifest.getVirtualFile().getPath().startsWith(buildDir.getPath())))
                return manifest;
        }
        return files[0];
    }

    public static  void register(final Module module, final String className,final String packageName){
        CommandProcessor.getInstance().executeCommand(module.getProject(), new Runnable() {
            public void run() {
                try {
                    PsiFile manifast = getManifastFile(module);
                    PsiElement element = getApplicationTag(manifast);
                    PsiElement activityElement = getActivityTag(manifast,className,packageName);
                    if (activityElement != null)
                        return;
                    XmlTagImpl application = (XmlTagImpl) element;
                    XmlTag activity =application.createChildTag("activity", application.getNamespace(), "", true);
                    activity.setAttribute("android:name",application.getNamespace(),className);
                    application.add(activity);
                } catch (IncorrectOperationException e) {
                    e.printStackTrace();
                }
            }
        }, "", null, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);

    }
    /**
     * Obtain all IDs from layout
     *
     * @param file
     * @return
     */
    public static PsiElement getApplicationTag(final PsiFile file) {
        final ArrayList<PsiElement> activity = new ArrayList<PsiElement>();
        file.accept(new XmlRecursiveElementVisitor() {

            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);

                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;

                    if (tag.getName().equalsIgnoreCase("application")) {
                        activity.add(element);
                    }
                }}
        });

        return activity.get(0);
    }
    /**
     * Obtain all IDs from layout
     *
     * @param file
     * @return
     */
    public static PsiElement getActivityTag(final PsiFile file, final String className, final String packageName) {
        final ArrayList<PsiElement> activity = new ArrayList<PsiElement>();
        file.accept(new XmlRecursiveElementVisitor() {

            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);

                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;

                    if (tag.getName().equalsIgnoreCase("activity") ) {
                        String activityClass = tag.getAttributeValue("android:name");
                        activityClass = (activityClass!= null && activityClass.startsWith("."))?packageName+activityClass:activityClass;
                        if (className.equals(activityClass))
                            activity.add(element);
                    }
                }}
        });
        if (activity.size() > 0)
            return activity.get(0);
        return  null;
    }

}
